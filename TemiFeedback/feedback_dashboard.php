<?php
/**
 * Feedback Dashboard for AllIsWell Hospital
 * View and manage all patient feedback from Temi Robot
 *
 * Access: https://alliswellhospital.com/TemiFeedback/feedback_dashboard.php
 */

// Enable error reporting
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Database configuration
define('DB_HOST', 'localhost');
define('DB_USER', 'your_db_user');       // Update with your credentials
define('DB_PASS', 'your_db_password');   // Update with your credentials
define('DB_NAME', 'alliswellhospital');

try {
    $conn = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);

    if ($conn->connect_error) {
        throw new Exception('Database connection failed: ' . $conn->connect_error);
    }

    $conn->set_charset("utf8mb4");

    // Get filter parameters
    $filter_rating = isset($_GET['rating']) ? intval($_GET['rating']) : 0;
    $filter_date = isset($_GET['date']) ? $conn->real_escape_string($_GET['date']) : '';
    $sort_by = isset($_GET['sort']) ? $conn->real_escape_string($_GET['sort']) : 'created_at DESC';
    $page = isset($_GET['page']) ? max(1, intval($_GET['page'])) : 1;
    $per_page = 20;
    $offset = ($page - 1) * $per_page;

    // Build WHERE clause
    $where_conditions = [];
    if ($filter_rating > 0) {
        $where_conditions[] = "rating = {$filter_rating}";
    }
    if (!empty($filter_date)) {
        $where_conditions[] = "DATE(created_at) = '{$filter_date}'";
    }

    $where_clause = !empty($where_conditions) ? 'WHERE ' . implode(' AND ', $where_conditions) : '';

    // Get total count
    $count_result = $conn->query("SELECT COUNT(*) as total FROM feedback {$where_clause}");
    $total_feedback = $count_result->fetch_assoc()['total'];
    $total_pages = ceil($total_feedback / $per_page);

    // Get average rating
    $avg_result = $conn->query("SELECT AVG(rating) as avg_rating FROM feedback {$where_clause}");
    $avg_rating = round($avg_result->fetch_assoc()['avg_rating'], 1);

    // Get feedback data
    $sql = "SELECT id, rating, comment, patient_id, created_at FROM feedback {$where_clause} ORDER BY {$sort_by} LIMIT {$offset}, {$per_page}";
    $feedback_result = $conn->query($sql);

    if (!$feedback_result) {
        throw new Exception('Query failed: ' . $conn->error);
    }

    $feedbacks = [];
    while ($row = $feedback_result->fetch_assoc()) {
        $feedbacks[] = $row;
    }

} catch (Exception $e) {
    $error_message = $e->getMessage();
}
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>AllIsWell Hospital - Feedback Dashboard</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
        }

        header {
            background: white;
            padding: 30px;
            border-radius: 10px;
            margin-bottom: 30px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }

        h1 {
            color: #333;
            margin-bottom: 10px;
        }

        .stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-top: 20px;
        }

        .stat-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px;
            border-radius: 8px;
            text-align: center;
        }

        .stat-value {
            font-size: 32px;
            font-weight: bold;
            margin-bottom: 5px;
        }

        .stat-label {
            font-size: 14px;
            opacity: 0.9;
        }

        .filters {
            background: white;
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 30px;
            display: flex;
            gap: 15px;
            flex-wrap: wrap;
            align-items: center;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }

        .filter-group {
            display: flex;
            gap: 10px;
            align-items: center;
        }

        label {
            font-weight: 600;
            color: #333;
        }

        select, input {
            padding: 8px 12px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
        }

        select:focus, input:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 5px rgba(102,126,234,0.5);
        }

        button {
            padding: 8px 20px;
            background: #667eea;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-weight: 600;
            transition: background 0.3s;
        }

        button:hover {
            background: #764ba2;
        }

        .feedback-table {
            background: white;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th {
            background: #f5f5f5;
            padding: 15px;
            text-align: left;
            font-weight: 600;
            color: #333;
            border-bottom: 2px solid #ddd;
        }

        td {
            padding: 15px;
            border-bottom: 1px solid #eee;
        }

        tr:hover {
            background: #f9f9f9;
        }

        .rating {
            display: flex;
            gap: 3px;
        }

        .star {
            font-size: 18px;
            color: #gold;
        }

        .star.filled {
            color: #ffd700;
        }

        .star.empty {
            color: #ddd;
        }

        .comment {
            max-width: 300px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }

        .date {
            font-size: 12px;
            color: #666;
        }

        .pagination {
            display: flex;
            gap: 10px;
            justify-content: center;
            margin-top: 30px;
        }

        .pagination a, .pagination span {
            padding: 8px 12px;
            border: 1px solid #ddd;
            border-radius: 5px;
            text-decoration: none;
            color: #667eea;
            transition: all 0.3s;
        }

        .pagination a:hover {
            background: #667eea;
            color: white;
        }

        .pagination .active {
            background: #667eea;
            color: white;
            border-color: #667eea;
        }

        .error {
            background: #fee;
            color: #c33;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
        }

        .empty-state {
            text-align: center;
            padding: 40px;
            color: #999;
        }
    </style>
</head>
<body>
    <div class="container">
        <header>
            <h1>🏥 AllIsWell Hospital - Feedback Dashboard</h1>
            <p>Real-time patient feedback from Temi Robot</p>

            <div class="stats">
                <div class="stat-card">
                    <div class="stat-value"><?php echo $total_feedback; ?></div>
                    <div class="stat-label">Total Feedbacks</div>
                </div>
                <div class="stat-card">
                    <div class="stat-value"><?php echo $avg_rating; ?>/5</div>
                    <div class="stat-label">Average Rating</div>
                </div>
            </div>
        </header>

        <?php if (isset($error_message)): ?>
        <div class="error">
            <strong>Error:</strong> <?php echo htmlspecialchars($error_message); ?>
        </div>
        <?php endif; ?>

        <div class="filters">
            <form method="get" style="display: flex; gap: 15px; flex-wrap: wrap; align-items: center; width: 100%;">
                <div class="filter-group">
                    <label>Rating:</label>
                    <select name="rating">
                        <option value="0">All Ratings</option>
                        <option value="5" <?php echo $filter_rating === 5 ? 'selected' : ''; ?>>⭐⭐⭐⭐⭐ 5 Stars</option>
                        <option value="4" <?php echo $filter_rating === 4 ? 'selected' : ''; ?>>⭐⭐⭐⭐ 4 Stars</option>
                        <option value="3" <?php echo $filter_rating === 3 ? 'selected' : ''; ?>>⭐⭐⭐ 3 Stars</option>
                        <option value="2" <?php echo $filter_rating === 2 ? 'selected' : ''; ?>>⭐⭐ 2 Stars</option>
                        <option value="1" <?php echo $filter_rating === 1 ? 'selected' : ''; ?>>⭐ 1 Star</option>
                    </select>
                </div>

                <div class="filter-group">
                    <label>Date:</label>
                    <input type="date" name="date" value="<?php echo htmlspecialchars($filter_date); ?>">
                </div>

                <button type="submit">Filter</button>
                <a href="?" style="padding: 8px 20px; background: #999; color: white; border-radius: 5px; text-decoration: none; cursor: pointer;">Clear</a>
            </form>
        </div>

        <div class="feedback-table">
            <?php if (empty($feedbacks)): ?>
            <div class="empty-state">
                <p>No feedback found</p>
            </div>
            <?php else: ?>
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Rating</th>
                        <th>Comment</th>
                        <th>Patient ID</th>
                        <th>Date & Time</th>
                    </tr>
                </thead>
                <tbody>
                    <?php foreach ($feedbacks as $feedback): ?>
                    <tr>
                        <td>#<?php echo $feedback['id']; ?></td>
                        <td>
                            <div class="rating">
                                <?php for ($i = 1; $i <= 5; $i++): ?>
                                <span class="star <?php echo $i <= $feedback['rating'] ? 'filled' : 'empty'; ?>">★</span>
                                <?php endfor; ?>
                            </div>
                            <small><?php echo $feedback['rating']; ?>/5</small>
                        </td>
                        <td>
                            <div class="comment" title="<?php echo htmlspecialchars($feedback['comment']); ?>">
                                <?php echo htmlspecialchars($feedback['comment']); ?>
                            </div>
                        </td>
                        <td><?php echo !empty($feedback['patient_id']) ? htmlspecialchars($feedback['patient_id']) : '-'; ?></td>
                        <td>
                            <div class="date">
                                <?php echo date('M d, Y', strtotime($feedback['created_at'])); ?><br>
                                <?php echo date('H:i:s', strtotime($feedback['created_at'])); ?>
                            </div>
                        </td>
                    </tr>
                    <?php endforeach; ?>
                </tbody>
            </table>
            <?php endif; ?>
        </div>

        <?php if ($total_pages > 1): ?>
        <div class="pagination">
            <?php if ($page > 1): ?>
            <a href="?page=1<?php echo $filter_rating > 0 ? "&rating={$filter_rating}" : ''; ?><?php echo !empty($filter_date) ? "&date={$filter_date}" : ''; ?>">« First</a>
            <a href="?page=<?php echo $page - 1; ?><?php echo $filter_rating > 0 ? "&rating={$filter_rating}" : ''; ?><?php echo !empty($filter_date) ? "&date={$filter_date}" : ''; ?>">‹ Previous</a>
            <?php endif; ?>

            <?php for ($i = max(1, $page - 2); $i <= min($total_pages, $page + 2); $i++): ?>
            <a href="?page=<?php echo $i; ?><?php echo $filter_rating > 0 ? "&rating={$filter_rating}" : ''; ?><?php echo !empty($filter_date) ? "&date={$filter_date}" : ''; ?>" class="<?php echo $i === $page ? 'active' : ''; ?>">
                <?php echo $i; ?>
            </a>
            <?php endfor; ?>

            <?php if ($page < $total_pages): ?>
            <a href="?page=<?php echo $page + 1; ?><?php echo $filter_rating > 0 ? "&rating={$filter_rating}" : ''; ?><?php echo !empty($filter_date) ? "&date={$filter_date}" : ''; ?>">Next ›</a>
            <a href="?page=<?php echo $total_pages; ?><?php echo $filter_rating > 0 ? "&rating={$filter_rating}" : ''; ?><?php echo !empty($filter_date) ? "&date={$filter_date}" : ''; ?>">Last »</a>
            <?php endif; ?>
        </div>
        <?php endif; ?>
    </div>
</body>
</html>

