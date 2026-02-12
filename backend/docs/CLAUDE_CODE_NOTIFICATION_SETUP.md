# Claude Code 작업 완료 알림 설정 가이드

Claude Code에서 백그라운드 작업이 완료되면 macOS 알림을 받을 수 있도록 설정하는 방법입니다.

## 개요

Claude Code의 Hook 기능을 사용하여 작업 완료 시 macOS 알림(배너 + 소리)을 받습니다.

## 설정 파일

### 1. Claude Code 설정 파일

**파일 경로**: `~/.claude/settings.json`

```json
{
  "hooks": {
    "PostToolUse": [
      {
        "hooks": [
          {
            "type": "command",
            "command": "bash ~/.claude/hooks/post-tool-notification.sh"
          }
        ]
      }
    ]
  }
}
```

### 2. 알림 스크립트

**파일 경로**: `~/.claude/hooks/post-tool-notification.sh`

```bash
#!/bin/bash
# Post Tool Use Notification Hook (without jq)

mkdir -p ~/.claude/logs
INPUT=$(cat)

# Extract tool_name using grep (no jq needed)
TOOL_NAME=$(echo "$INPUT" | grep -o '"tool_name":"[^"]*"' | head -1 | sed 's/"tool_name":"//;s/"$//')

echo "[$(date)] tool_name=$TOOL_NAME" >> ~/.claude/logs/notification-debug.log

# TaskOutput 완료 시 알림
if [ "$TOOL_NAME" = "TaskOutput" ]; then
  # Check if status is completed
  if echo "$INPUT" | grep -q '"status":"completed"'; then
    osascript -e 'display notification "작업이 완료되었습니다" with title "Claude Code" subtitle "Complete" sound name "Glass"'
    echo "[$(date)] TaskOutput notification sent" >> ~/.claude/logs/notification-debug.log
  fi
fi

# Task (Agent) 완료 시 알림
if [ "$TOOL_NAME" = "Task" ]; then
  osascript -e 'display notification "Agent 작업 완료" with title "Claude Code" sound name "Glass"'
  echo "[$(date)] Task notification sent" >> ~/.claude/logs/notification-debug.log
fi

echo '{"continue": true}'
exit 0
```

## 설치 방법

### 1. 디렉토리 생성

```bash
mkdir -p ~/.claude/hooks
mkdir -p ~/.claude/logs
```

### 2. 스크립트 파일 생성

```bash
cat > ~/.claude/hooks/post-tool-notification.sh << 'EOF'
#!/bin/bash
# Post Tool Use Notification Hook (without jq)

mkdir -p ~/.claude/logs
INPUT=$(cat)

# Extract tool_name using grep (no jq needed)
TOOL_NAME=$(echo "$INPUT" | grep -o '"tool_name":"[^"]*"' | head -1 | sed 's/"tool_name":"//;s/"$//')

echo "[$(date)] tool_name=$TOOL_NAME" >> ~/.claude/logs/notification-debug.log

# TaskOutput 완료 시 알림
if [ "$TOOL_NAME" = "TaskOutput" ]; then
  if echo "$INPUT" | grep -q '"status":"completed"'; then
    osascript -e 'display notification "작업이 완료되었습니다" with title "Claude Code" subtitle "Complete" sound name "Glass"'
    echo "[$(date)] TaskOutput notification sent" >> ~/.claude/logs/notification-debug.log
  fi
fi

# Task (Agent) 완료 시 알림
if [ "$TOOL_NAME" = "Task" ]; then
  osascript -e 'display notification "Agent 작업 완료" with title "Claude Code" sound name "Glass"'
  echo "[$(date)] Task notification sent" >> ~/.claude/logs/notification-debug.log
fi

echo '{"continue": true}'
exit 0
EOF

chmod +x ~/.claude/hooks/post-tool-notification.sh
```

### 3. settings.json 수정

`~/.claude/settings.json` 파일에 hooks 설정 추가:

```bash
# 기존 설정이 있다면 수동으로 hooks 부분 추가
# 또는 새로 생성:
cat > ~/.claude/settings.json << 'EOF'
{
  "hooks": {
    "PostToolUse": [
      {
        "hooks": [
          {
            "type": "command",
            "command": "bash ~/.claude/hooks/post-tool-notification.sh"
          }
        ]
      }
    ]
  }
}
EOF
```

## 알림 발생 조건

| 이벤트 | 알림 내용 |
|--------|----------|
| 백그라운드 작업 완료 (TaskOutput) | "작업이 완료되었습니다" |
| Agent 작업 완료 (Task) | "Agent 작업 완료" |

## 디버깅

### 로그 확인

```bash
tail -f ~/.claude/logs/notification-debug.log
```

### 알림 수동 테스트

```bash
osascript -e 'display notification "테스트" with title "Claude Code" sound name "Glass"'
```

## 주의사항

1. **jq 사용 금지**: Claude Code 훅 환경에서 `jq`가 설치되어 있지 않을 수 있음. `grep`과 `sed`로 JSON 파싱.

2. **반드시 JSON 반환**: 스크립트 끝에 `echo '{"continue": true}'`가 있어야 Claude Code가 정상 동작함.

3. **macOS 알림 권한**: 시스템 설정에서 터미널/스크립트 에디터의 알림 권한이 허용되어 있어야 함.

## 사용 가능한 Hook 이벤트

| 이벤트 | 설명 |
|--------|------|
| SessionStart | Claude Code 세션 시작 시 |
| PreToolUse | 도구 실행 전 |
| PostToolUse | 도구 실행 후 |

## 참고

- [Claude Code Hooks 공식 문서](https://docs.anthropic.com/en/docs/claude-code/hooks)
- 알림 소리 종류: Glass, Basso, Blow, Bottle, Frog, Funk, Hero, Morse, Ping, Pop, Purr, Sosumi, Submarine, Tink
