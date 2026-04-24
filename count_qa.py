import json

with open('Hospital temi Dataset.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

# Count Sheet1 (English)
sheet1 = data.get('Sheet1', [])
valid_qa = []

for item in sheet1:
    if not item:
        continue
    question = (item.get('HOSPITAL TEMI') or '').strip()
    answer = (item.get('Column2') or '').strip()

    if question and answer:
        valid_qa.append((question, answer))

# Count Sheet2 (Hindi)
sheet2 = data.get('Sheet2', [])
hindi_qa = []

for item in sheet2:
    if not item:
        continue
    question = (item.get('Hindi Questions') or '').strip()
    answer = (item.get('Column2') or '').strip()

    if question and answer:
        hindi_qa.append((question, answer))

print(f"English Q&As (Sheet1): {len(valid_qa)}")
print(f"Hindi Q&As (Sheet2): {len(hindi_qa)}")
print(f"TOTAL Q&As: {len(valid_qa) + len(hindi_qa)}")
print(f"\nFirst 10 English questions:")
for i, (q, a) in enumerate(valid_qa[:10], 1):
    print(f"{i}. {q}")

