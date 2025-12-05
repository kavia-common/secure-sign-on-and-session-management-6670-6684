#!/bin/bash
cd /home/kavia/workspace/code-generation/secure-sign-on-and-session-management-6670-6684/auth_backend
./gradlew checkstyleMain
LINT_EXIT_CODE=$?
if [ $LINT_EXIT_CODE -ne 0 ]; then
   exit 1
fi

