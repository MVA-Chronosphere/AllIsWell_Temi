#!/usr/bin/env python3
"""
Hospital Knowledge Base Loader
Loads all 294 Q&As from generated_knowledge_base.kt into HospitalKnowledgeBase.kt
"""

import re
import sys

def load_all_qa():
    generated_file = "/Users/mva357/AndroidStudioProjects/AlliswellTemi/generated_knowledge_base.kt"
    kb_file = "/Users/mva357/AndroidStudioProjects/AlliswellTemi/app/src/main/java/com/example/alliswelltemi/data/HospitalKnowledgeBase.kt"

    print("🚀 Hospital Knowledge Base - Full Production Loader")
    print("=" * 60)

    # Read the generated file
    print("\n📥 Reading generated_knowledge_base.kt...")
    with open(generated_file, 'r', encoding='utf-8') as f:
        generated_content = f.read()

    # Extract the listOf(...) content
    # Find the content between listOf( and final )
    match = re.search(r'private val qaDatabase = listOf\((.*)\)', generated_content, re.DOTALL)
    if not match:
        print("❌ Error: Could not find qaDatabase in generated file")
        sys.exit(1)

    qa_content = match.group(1)

    # Count Q&As
    qa_count = qa_content.count('KnowledgeBaseQA(')
    print(f"✅ Found {qa_count} Q&A pairs")

    # Read the KB file
    print("\n📖 Reading HospitalKnowledgeBase.kt...")
    with open(kb_file, 'r', encoding='utf-8') as f:
        kb_content = f.read()

    # Create backup
    print("\n💾 Creating backup...")
    with open(kb_file + ".backup", 'w', encoding='utf-8') as f:
        f.write(kb_content)
    print(f"✅ Backup saved: {kb_file}.backup")

    # Replace the qaDatabase listOf content
    print("\n🔄 Injecting all 294 Q&As...")

    # Find the existing listOf and replace it
    pattern = r'(    private val qaDatabase = listOf\()(.*?)(\n    \))'

    replacement = r'\1' + qa_content + r'\3'

    new_kb_content = re.sub(pattern, replacement, kb_content, flags=re.DOTALL)

    # Verify replacement
    if 'KnowledgeBaseQA(' not in new_kb_content or new_kb_content.count('KnowledgeBaseQA(') < 100:
        print("❌ Error: Replacement may have failed")
        sys.exit(1)

    # Count Q&As in new content
    new_count = new_kb_content.count('KnowledgeBaseQA(')
    print(f"✅ Successfully injected {new_count} Q&A pairs")

    # Write the new content
    print("\n✍️ Writing to HospitalKnowledgeBase.kt...")
    with open(kb_file, 'w', encoding='utf-8') as f:
        f.write(new_kb_content)
    print("✅ File updated successfully")

    print("\n" + "=" * 60)
    print("✅ SUCCESS! Hospital Knowledge Base is Ready")
    print("=" * 60)
    print(f"\n📊 Status:")
    print(f"   • Q&A Pairs Loaded: {new_count}")
    print(f"   • Knowledge Base: FULLY INTEGRATED")
    print(f"   • File: HospitalKnowledgeBase.kt")
    print(f"\n🚀 Next Steps:")
    print(f"   1. Build: ./gradlew clean build")
    print(f"   2. Deploy: ./gradlew installDebug")
    print(f"   3. Test voice queries")
    print()

if __name__ == "__main__":
    try:
        load_all_qa()
    except Exception as e:
        print(f"\n❌ Error: {e}")
        sys.exit(1)

