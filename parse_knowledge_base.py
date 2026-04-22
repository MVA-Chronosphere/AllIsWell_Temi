#!/usr/bin/env python3
"""
Hospital Knowledge Base Parser
Converts Hospital temi Dataset.json to HospitalKnowledgeBase.kt format
Automatically extracts Q&A pairs and generates Kotlin code
"""

import json
import re
import sys

def extract_keywords(text):
    """Extract keywords from question and answer"""
    if not text:
        return []

    # Remove common words and extract meaningful terms
    words = text.lower().split()
    stop_words = {'what', 'is', 'the', 'a', 'an', 'are', 'do', 'does', 'can', 'i', 'you', 'your', 'and', 'or', 'in', 'on', 'at', 'to', 'for', 'of', 'with', 'by'}

    keywords = []
    for word in words:
        # Clean punctuation
        word = re.sub(r'[^\w]', '', word)
        if word and word not in stop_words and len(word) > 2:
            keywords.append(word)

    # Remove duplicates and limit
    return list(set(keywords))[:10]  # Max 10 keywords per QA

def categorize_qa(question):
    """Auto-categorize Q&A based on question content"""
    q_lower = question.lower()

    if any(word in q_lower for word in ['insurance', 'claim', 'policy', 'cashless', 'reimbursement']):
        return 'insurance'
    elif any(word in q_lower for word in ['doctor', 'specialist', 'department', 'consultation']):
        return 'departments'
    elif any(word in q_lower for word in ['appointment', 'book', 'schedule', 'registration']):
        return 'appointments'
    elif any(word in q_lower for word in ['hour', 'open', 'close', 'time', 'emergency', '24']):
        return 'hospital_info'
    elif any(word in q_lower for word in ['location', 'address', 'where', 'direction', 'parking']):
        return 'facilities'
    elif any(word in q_lower for word in ['maternity', 'pregnancy', 'obstetric', 'gynaecol']):
        return 'maternity'
    elif any(word in q_lower for word in ['lab', 'test', 'diagnostic', 'pathology']):
        return 'diagnostics'
    elif any(word in q_lower for word in ['medicine', 'pharmacy', 'drug', 'prescription']):
        return 'pharmacy'
    else:
        return 'general'

def parse_hospital_dataset(json_file):
    """Parse the Hospital temi Dataset.json"""

    try:
        with open(json_file, 'r', encoding='utf-8') as f:
            data = json.load(f)
    except Exception as e:
        print(f"Error reading JSON: {e}", file=sys.stderr)
        return []

    qa_list = []
    sheet_data = data.get('Sheet1', [])

    current_question = None
    for i, item in enumerate(sheet_data):
        if not item:  # Skip null items
            continue

        question = item.get('HOSPITAL TEMI', '') or ''
        answer = item.get('Column2', '') or ''
        question = question.strip()
        answer = answer.strip()

        # Skip headers and category markers
        if not question or question.isupper() or ':' not in question and not answer:
            continue

        # If we have an answer, create QA pair
        if answer:
            keywords = extract_keywords(question + ' ' + answer)
            category = categorize_qa(question)

            qa_pair = {
                'id': f"qa_{len(qa_list) + 1}",
                'question': question,
                'answer': answer,
                'keywords': keywords,
                'category': category,
                'language': 'en'
            }
            qa_list.append(qa_pair)

    return qa_list

def generate_kotlin_code(qa_list):
    """Generate Kotlin code for HospitalKnowledgeBase.kt"""

    kotlin_code = 'private val qaDatabase = listOf(\n'

    for qa in qa_list:
        # Escape quotes in text
        question = qa['question'].replace('"', '\\"')
        answer = qa['answer'].replace('"', '\\"')

        # Limit answer length to avoid extremely long strings
        if len(answer) > 500:
            answer = answer[:497] + '...'

        keywords_str = ', '.join([f'"{k}"' for k in qa['keywords']])

        kotlin_code += f'''    KnowledgeBaseQA(
        id = "{qa['id']}",
        question = "{question}",
        answer = "{answer}",
        keywords = listOf({keywords_str}),
        category = "{qa['category']}",
        language = "{qa['language']}"
    ),
'''

    kotlin_code += ')\n'

    return kotlin_code

def main():
    json_file = "/Users/mva357/AndroidStudioProjects/AlliswellTemi/Hospital temi Dataset.json"
    output_file = "/Users/mva357/AndroidStudioProjects/AlliswellTemi/generated_knowledge_base.kt"

    print("Parsing Hospital temi Dataset.json...")
    qa_list = parse_hospital_dataset(json_file)

    print(f"Found {len(qa_list)} Q&A pairs")

    # Print summary by category
    categories = {}
    for qa in qa_list:
        cat = qa['category']
        categories[cat] = categories.get(cat, 0) + 1

    print("\nQ&A Distribution by Category:")
    for cat, count in sorted(categories.items()):
        print(f"  {cat}: {count}")

    print(f"\nGenerating Kotlin code...")
    kotlin_code = generate_kotlin_code(qa_list)

    # Save to file
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write(kotlin_code)

    print(f"✅ Generated {output_file}")
    print(f"\nFirst few lines of generated code:")
    print('\n'.join(kotlin_code.split('\n')[:20]))

    return len(qa_list)

if __name__ == '__main__':
    count = main()
    sys.exit(0 if count > 0 else 1)


