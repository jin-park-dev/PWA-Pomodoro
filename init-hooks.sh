#!/usr/bin/env bash
set -e

git config core.hooksPath dev/hooks
chmod +x dev/hooks/pre-push