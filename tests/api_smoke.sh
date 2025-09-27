#!/usr/bin/env bash
BASE_URL="http://localhost:8080/api"
OUT_FILE="tests/api_smoke_results.txt"
rm -f "$OUT_FILE"

run(){
  local method="$1"
  local path="$2"
  local body="$3"
  local url="${BASE_URL}${path}"
  echo "=== ${method} ${path}" >> "$OUT_FILE"
  if [ -n "$body" ]; then
    echo "Request: $body" >> "$OUT_FILE"
    curl -s -w "\nHTTP_STATUS:%{http_code}\n" -X "$method" "$url" -H 'Content-Type: application/json' -d "$body" >> "$OUT_FILE"
  else
    curl -s -w "\nHTTP_STATUS:%{http_code}\n" -X "$method" "$url" >> "$OUT_FILE"
  fi
  echo "" >> "$OUT_FILE"
}

# Auth module
auth_body_send='{"account":"13800000000","accountType":"phone","businessType":"register"}'
run POST /auth/send-code "$auth_body_send"
auth_register='{"account":"13800000000","code":"123456","nickname":"测试用户"}'
run POST /auth/register-by-code "$auth_register"
auth_login='{"loginAccount":"13800000000","loginType":"code","code":"123456"}'
run POST /auth/login "$auth_login"
run POST /auth/logout ""
run GET /auth/sessions ""
run POST /auth/kick-other-sessions ""
run GET /auth/health ""

# User module
run GET /user/self/info ""
run GET /user/info/1 ""
user_update='{"nickname":"新昵称","avatar":"https://example.com/avatar.jpg"}'
run PUT /user/update "$user_update"
change_password='{"oldPassword":"123456","newPassword":"654321","confirmPassword":"654321"}'
run POST /user/changePassword "$change_password"
init_password='{"password":"123456","confirmPassword":"123456"}'
run POST /user/initializePassword "$init_password"
reset_sms='{"phone":"13800000000","code":"123456","password":"newpass123","confirmPassword":"newpass123"}'
run POST /user/resetPasswordWithSms "$reset_sms"
run GET "/user/check/username?username=testuser" ""
run GET "/user/check/phone?phone=13800000000" ""
run GET "/user/check/email?email=test@example.com" ""

# Knock module
knock_manual='{"knockCount":10,"knockSound":"default","sessionDuration":60}'
run POST /knock/manual "$knock_manual"
auto_start='{"duration":300,"knockInterval":1000,"knockSound":"default"}'
run POST /knock/auto/start "$auto_start"
auto_stop='{"sessionId":"auto_knock_sess_123","actualDuration":250}'
run POST /knock/auto/stop "$auto_stop"
auto_heartbeat='{"sessionId":"auto_knock_sess_123","currentKnockCount":150}'
run POST /knock/auto/heartbeat "$auto_heartbeat"
run GET /knock/stats ""
run GET "/knock/stats/periods?referenceDate=2025-09-27" ""
run GET /knock/auto/status ""

# Meditation module
med_purchase='{"planType":"DAY"}'
run POST /meditation/subscription/purchase "$med_purchase"
run GET /meditation/subscription/status ""
med_start='{"plannedDuration":600,"withKnock":1,"knockFrequency":80}'
run POST /meditation/session/start "$med_start"
med_finish='{"sessionId":"meditation_sess_123","actualDuration":580,"withKnock":1,"knockFrequency":80,"moodCode":"happy","insightText":"保持平常心"}'
run POST /meditation/session/finish "$med_finish"
med_discard='{"sessionId":"meditation_sess_123"}'
run POST /meditation/session/discard "$med_discard"
run GET /meditation/stats/summary ""
run GET "/meditation/stats/month?month=2025-09" ""
run GET /meditation/config/default ""
update_pref='{"defaultDuration":600,"defaultWithKnock":1,"defaultKnockFrequency":80}'
run PUT /meditation/config/default "$update_pref"

# Scripture module
run GET "/scripture/list" ""
run GET /scripture/1 ""
run GET /scripture/hot ""
run GET /scripture/type/classic ""
run GET "/scripture/search?keyword=心经" ""
run POST /scripture/1/read ""

# Merit module
run POST /merit/balance ""
exchange='{"exchangeAmount":100,"exchangeRate":1}'
run POST /merit/exchange "$exchange"
run GET "/merit/history" ""
run GET "/merit/summary" ""
run GET /merit/stats/1 ""
run GET "/merit/records?userId=1" ""
run GET /merit/today/1 ""
run GET /merit/weekly/1 ""
run GET /merit/monthly/1 ""

# Rankings
run GET "/rankings/daily?limit=5" ""
run GET "/rankings/weekly?limit=5" ""
run GET "/rankings/total?limit=5" ""
run GET /rankings/my ""

# 文件上传占位（无文件，仅确认响应）
run POST /file/upload ""

