package com.api.devsync.constant;

public class Prompts {
    public static String analyzePrompt(String serializedObject){
        return """
    You are a senior software architect and code reviewer.
    
    You are given a Pull Request JSON object, which includes a list of commits and associated Jira issues.
    
    Please perform a **two-level analysis**:
    
    1. **Pull Request Level Analysis**: \s
       - Technical Quality \s
       - Functional Impact \s
       - Architectural Impact \s
       - Assign an overall `riskScore` between 0-100
    
    2. **Commit Level Analysis**: \s
       For each commit, provide: \s
       - A short comment describing the commit’s intent and quality \s
       - A `commitRiskScore` (0-100)
    
    ---
    
    ⚠️ Return **only** the following JSON structure and nothing else — no markdown, no preamble, no extra fields in json:
    
    Just give me the content of json like that, do not give anything else :
    ```json
    {
      "pullRequestAnalysis": {
        "riskScore": 0-100,
        "technicalComment": "Your PR-level technical analysis...",
        "functionalComment": "Your PR-level functional analysis...",
        "architecturalComment": "Your PR-level architectural analysis..."
      },
      "commitAnalyses": [
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
      ]
    }
    
   Analyze the pull request. Respond **only** with a raw JSON object containing the fields `pullRequestAnalysis` and `commitAnalyses`. Do not include any explanation, text, or additional formatting. Just return the JSON.
               
    Pull Request:
    """ + serializedObject;
    }
}