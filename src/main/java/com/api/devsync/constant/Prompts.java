package com.api.devsync.constant;

public class Prompts {
    public static String prAnalyzePrompt(String serializedObject){
        return """
You are a senior software architect and code reviewer.

You are given a Pull Request JSON object, including:
- Pull request metadata
- Commits
- File changes (diffs included)

TASK:
Analyze the PR and all commits, then respond ONLY in valid JSON.

OUTPUT FORMAT:
{
  "pullRequestAnalysis": {
    "riskScore": <integer 0-100>,
    "riskReason:" : "<detailed risk reason>",
    "technicalComment": "<detailed technical comment>",
    "functionalComment": "<detailed functional comment>",
    "architecturalComment": "<detailed architectural comment>"
  },
  "commitAnalyses": [
    {
      "hash": "<commit hash>",
      "message": "<commit message>",
      "author": "<author>",
      "technicalComment": "<detailed technical comment>",
      "functionalComment": "<detailed functional comment>",
      "architecturalComment": "<detailed architectural comment>",
      "riskScore": <integer 0-100>,
      "riskReason:" : "<detailed risk reason>",
    }
  ]
}

RULES:
- Respond ONLY with valid JSON.
- Do NOT truncate.
- Ensure all arrays and objects are closed.
- If input is too large, process commits in summarized form but keep JSON complete.

INPUT:
%s
Pull Request:
""" + serializedObject;
    }


    public static String commitAnalyzePrompt(String serializedObject){
        return """
You are a **senior software architect** reviewing a Pull Request (JSON includes commits and (more important!)full code changes).

Perform **two-level analysis** strictly based on code and commit content:

1. **Pull Request Level**:
   - Technical Quality
   - Functional Impact
   - Architectural Impact
   - Overall `riskScore` (0–100)

2. **Commit Level** (for each commit):
   - Short intent/quality comment
   - `commitRiskScore` (0–100)

⚠ **STRICT OUTPUT RULES**  
- Respond **only** with raw JSON (no markdown, no text, no explanation)  
- Output format (exact field names, no extra fields):

"commitAnalyses": {
        {
          "hash": "commit hash here",
          "message": "commit message here",
          "technicalComment": "Your PR-level technical analysis...",
          "functionalComment": "Your PR-level functional analysis...",
          "architecturalComment": "Your PR-level architectural analysis..."
          "commitRiskScore": 0-100,
          "commit" : 
          {
            "hash": "commit hash here"
          }
        },
        ...
      }

Analyze the PR and commits **including all provided code**.  
Return **only JSON** in the exact schema above.  
Return only valid JSON. Do not cut off mid-string. Ensure all quotes are closed.
If output is too long, summarize commit list but keep valid JSON format.

Pull Request:
""" + serializedObject;
    }
}

