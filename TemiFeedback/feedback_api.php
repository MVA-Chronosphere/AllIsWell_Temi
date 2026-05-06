<?php
/**
 * Feedback API Endpoint for AllIsWell Hospital
 * Receives feedback from Android Temi Robot and stores in MySQL database
 *
 * Usage: POST to https://alliswellhospital.com/TemiFeedback/feedback_api.php
 *
 * Expected JSON payload:
 * {
 *     "rating": 5,
 *     "comment": "Great service!",
 *     "timestamp": "2026-05-06 12:30:45",
 *     "patient_id": "optional_patient_id"
 * }
 */

// Enable error reporting for debugging
error_reporting(E_ALL);
ini_set('display_errors', 0);  // Don't display errors to client
ini_set('log_errors', 1);
ini_set('error_log', '/var/log/php-errors.log');

// Set JSON response header
header('Content-Type: application/json');

// Allow CORS if needed
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit(json_encode(['status' => 'success', 'message' => 'CORS preflight passed']));
}

// Database configuration
// IMPORTANT: Update these with your actual database credentials from Hosting India
define('DB_HOST', 'localhost');          // Usually localhost on shared hosting
define('DB_USER', 'your_db_user');       // From hosting control panel
define('DB_PASS', 'your_db_password');   // From hosting control panel
define('DB_NAME', 'alliswellhospital');  // Your database name

try {
    // Create connection
    $conn = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);

    // Check connection
    if ($conn->connect_error) {
        throw new Exception('Database connection failed: ' . $conn->connect_error);
    }

    // Set charset to utf8
    $conn->set_charset("utf8");

    // Only process POST requests
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        http_response_code(405);
        echo json_encode([
            'status' => 'error',
            'message' => 'Only POST requests are allowed'
        ]);
        exit;
    }

    // Get JSON payload
    $json_input = file_get_contents('php://input');
    $data = json_decode($json_input, true);

    if ($data === null) {
        throw new Exception('Invalid JSON payload');
    }

    // Validate required fields
    if (!isset($data['rating']) || !isset($data['comment'])) {
        http_response_code(400);
        echo json_encode([
            'status' => 'error',
            'message' => 'Missing required fields: rating, comment'
        ]);
        exit;
    }

    // Sanitize inputs
    $rating = intval($data['rating']);
    $comment = $conn->real_escape_string(trim($data['comment']));
    $timestamp = isset($data['timestamp']) ? $conn->real_escape_string($data['timestamp']) : date('Y-m-d H:i:s');
    $patient_id = isset($data['patient_id']) ? $conn->real_escape_string($data['patient_id']) : '';

    // Validate rating (1-5)
    if ($rating < 1 || $rating > 5) {
        http_response_code(400);
        echo json_encode([
            'status' => 'error',
            'message' => 'Rating must be between 1 and 5'
        ]);
        exit;
    }

    // Validate comment length
    if (strlen($comment) > 500) {
        http_response_code(400);
        echo json_encode([
            'status' => 'error',
            'message' => 'Comment is too long (max 500 characters)'
        ]);
        exit;
    }

    // Check if table exists, if not create it
    $table_check = $conn->query("SHOW TABLES LIKE 'feedback'");
    if ($table_check->num_rows === 0) {
        $create_table_sql = "
            CREATE TABLE feedback (
                id INT AUTO_INCREMENT PRIMARY KEY,
                rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
                comment TEXT,
                patient_id VARCHAR(100),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                ip_address VARCHAR(45),
                user_agent VARCHAR(255),
                INDEX idx_rating (rating),
                INDEX idx_created (created_at)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
        ";

        if (!$conn->query($create_table_sql)) {
            throw new Exception('Failed to create feedback table: ' . $conn->error);
        }
    }

    // Get client IP
    $ip_address = $_SERVER['REMOTE_ADDR'];
    if (!empty($_SERVER['HTTP_CLIENT_IP'])) {
        $ip_address = $_SERVER['HTTP_CLIENT_IP'];
    } elseif (!empty($_SERVER['HTTP_X_FORWARDED_FOR'])) {
        $ip_address = explode(',', $_SERVER['HTTP_X_FORWARDED_FOR'])[0];
    }

    $user_agent = substr($_SERVER['HTTP_USER_AGENT'] ?? '', 0, 255);

    // Insert feedback into database
    $insert_sql = "
        INSERT INTO feedback (rating, comment, patient_id, ip_address, user_agent)
        VALUES (?, ?, ?, ?, ?)
    ";

    $stmt = $conn->prepare($insert_sql);
    if (!$stmt) {
        throw new Exception('Prepare failed: ' . $conn->error);
    }

    // Bind parameters (i = integer, s = string)
    $stmt->bind_param('issss', $rating, $comment, $patient_id, $ip_address, $user_agent);

    if (!$stmt->execute()) {
        throw new Exception('Execute failed: ' . $stmt->error);
    }

    $feedback_id = $stmt->insert_id;
    $stmt->close();

    // Success response
    http_response_code(201);
    echo json_encode([
        'status' => 'success',
        'message' => 'Feedback submitted successfully',
        'feedback_id' => $feedback_id,
        'timestamp' => date('Y-m-d H:i:s')
    ]);

} catch (Exception $e) {
    // Error response
    http_response_code(500);
    error_log('Feedback API Error: ' . $e->getMessage());
    echo json_encode([
        'status' => 'error',
        'message' => 'Failed to submit feedback: ' . $e->getMessage()
    ]);
} finally {
    if (isset($conn)) {
        $conn->close();
    }
}
?>

