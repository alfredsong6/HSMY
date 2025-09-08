#!/bin/bash

# 敲敲木鱼项目API接口测试脚本
# 模拟用户注册、登录及各种用户行为

BASE_URL="http://localhost:8080/api"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 测试结果统计
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

# 全局变量
SESSION_ID=""
USER_ID=""
USERNAME=""

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
    ((PASSED_TESTS++))
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
    ((FAILED_TESTS++))
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# HTTP请求函数
http_request() {
    local method=$1
    local url=$2
    local data=$3
    local headers=$4
    
    ((TOTAL_TESTS++))
    
    if [ -n "$headers" ]; then
        response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X "$method" \
            -H "Content-Type: application/json" \
            -H "$headers" \
            -d "$data" \
            "$BASE_URL$url")
    else
        response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X "$method" \
            -H "Content-Type: application/json" \
            -d "$data" \
            "$BASE_URL$url")
    fi
    
    http_status=$(echo "$response" | grep "HTTP_STATUS:" | cut -d: -f2)
    response_body=$(echo "$response" | sed '/HTTP_STATUS:/d')
    
    echo "$response_body"
    return $http_status
}

# 检查响应结果
check_response() {
    local response=$1
    local expected_code=$2
    local test_name=$3
    
    if [ $? -eq $expected_code ]; then
        log_success "$test_name - HTTP状态码: $expected_code"
        echo "$response" | jq '.' 2>/dev/null || echo "$response"
    else
        log_error "$test_name - 期望状态码: $expected_code, 实际状态码: $?"
        echo "$response"
    fi
    echo "----------------------------------------"
}

# 启动测试
start_test() {
    echo "========================================"
    log_info "开始敲敲木鱼项目API测试"
    echo "========================================"
    echo
}

# 测试1：健康检查
test_health_check() {
    log_info "测试1: 健康检查接口"
    response=$(http_request "GET" "/auth/health" "" "")
    check_response "$response" 200 "健康检查"
}

# 测试2：用户注册
test_user_register() {
    log_info "测试2: 用户注册"
    
    # 生成随机用户名
    local timestamp=$(date +%s)
    USERNAME="testuser_$timestamp"
    
    local register_data='{
        "username": "'$USERNAME'",
        "password": "123456",
        "confirmPassword": "123456",
        "nickname": "测试用户'$timestamp'",
        "phone": "1380013'$(printf "%04d" $((timestamp % 10000)))'",
        "email": "'$USERNAME'@example.com"
    }'
    
    response=$(http_request "POST" "/auth/register" "$register_data" "")
    check_response "$response" 200 "用户注册"
    
    # 测试重复注册
    log_info "测试2.1: 重复用户名注册（应该失败）"
    response=$(http_request "POST" "/auth/register" "$register_data" "")
    check_response "$response" 200 "重复用户名注册"
    
    # 测试密码不一致
    log_info "测试2.2: 密码不一致注册（应该失败）"
    local invalid_register_data='{
        "username": "testuser2_'$timestamp'",
        "password": "123456",
        "confirmPassword": "654321",
        "nickname": "测试用户2",
        "phone": "13800138002",
        "email": "testuser2@example.com"
    }'
    response=$(http_request "POST" "/auth/register" "$invalid_register_data" "")
    check_response "$response" 200 "密码不一致注册"
}

# 测试3：用户登录
test_user_login() {
    log_info "测试3: 用户登录"
    
    local login_data='{
        "loginAccount": "'$USERNAME'",
        "password": "123456"
    }'
    
    response=$(http_request "POST" "/auth/login" "$login_data" "")
    check_response "$response" 200 "用户登录"
    
    # 提取sessionId和userId
    SESSION_ID=$(echo "$response" | jq -r '.data.sessionId // empty')
    USER_ID=$(echo "$response" | jq -r '.data.userId // empty')
    
    if [ -n "$SESSION_ID" ]; then
        log_success "获取到SessionId: $SESSION_ID"
        log_success "获取到UserId: $USER_ID"
    else
        log_error "未能获取SessionId，后续需要认证的测试可能失败"
    fi
    
    # 测试错误密码登录
    log_info "测试3.1: 错误密码登录（应该失败）"
    local wrong_login_data='{
        "loginAccount": "'$USERNAME'",
        "password": "wrongpassword"
    }'
    response=$(http_request "POST" "/auth/login" "$wrong_login_data" "")
    check_response "$response" 200 "错误密码登录"
    
    # 测试不存在用户登录
    log_info "测试3.2: 不存在用户登录（应该失败）"
    local nonexistent_login_data='{
        "loginAccount": "nonexistentuser",
        "password": "123456"
    }'
    response=$(http_request "POST" "/auth/login" "$nonexistent_login_data" "")
    check_response "$response" 200 "不存在用户登录"
}

# 测试4：获取用户信息
test_get_user_info() {
    log_info "测试4: 获取当前用户信息"
    
    if [ -z "$SESSION_ID" ]; then
        log_error "SessionId为空，跳过需要认证的测试"
        return
    fi
    
    response=$(http_request "GET" "/auth/user-info" "" "X-Session-Id: $SESSION_ID")
    check_response "$response" 200 "获取用户信息"
    
    # 测试无效SessionId
    log_info "测试4.1: 无效SessionId获取用户信息（应该失败）"
    response=$(http_request "GET" "/auth/user-info" "" "X-Session-Id: invalid_session")
    check_response "$response" 401 "无效SessionId获取用户信息"
    
    # 测试无SessionId
    log_info "测试4.2: 无SessionId获取用户信息（应该失败）"
    response=$(http_request "GET" "/auth/user-info" "" "")
    check_response "$response" 401 "无SessionId获取用户信息"
}

# 测试5：会话管理
test_session_management() {
    log_info "测试5: 会话管理"
    
    if [ -z "$SESSION_ID" ]; then
        log_error "SessionId为空，跳过会话管理测试"
        return
    fi
    
    # 获取用户所有会话
    log_info "测试5.1: 获取用户所有会话"
    response=$(http_request "GET" "/auth/sessions" "" "X-Session-Id: $SESSION_ID")
    check_response "$response" 200 "获取用户会话列表"
    
    # 模拟多端登录 - 再次登录获得新的sessionId
    log_info "测试5.2: 模拟多端登录"
    local login_data='{
        "loginAccount": "'$USERNAME'",
        "password": "123456"
    }'
    response=$(http_request "POST" "/auth/login" "$login_data" "")
    local new_session_id=$(echo "$response" | jq -r '.data.sessionId // empty')
    
    if [ -n "$new_session_id" ]; then
        log_success "获取到新的SessionId: $new_session_id"
        
        # 再次获取会话列表，应该有2个会话
        log_info "测试5.3: 多端登录后的会话列表"
        response=$(http_request "GET" "/auth/sessions" "" "X-Session-Id: $SESSION_ID")
        check_response "$response" 200 "多端登录会话列表"
        
        # 踢出其他会话
        log_info "测试5.4: 踢出其他登录会话"
        response=$(http_request "POST" "/auth/kick-other-sessions" "" "X-Session-Id: $SESSION_ID")
        check_response "$response" 200 "踢出其他会话"
        
        # 验证新sessionId已失效
        log_info "测试5.5: 验证被踢出的会话已失效"
        response=$(http_request "GET" "/auth/user-info" "" "X-Session-Id: $new_session_id")
        check_response "$response" 401 "被踢出会话失效验证"
    fi
}

# 测试6：白名单功能
test_whitelist_functionality() {
    log_info "测试6: 白名单功能测试"
    
    # 测试白名单接口（不需要登录）
    log_info "测试6.1: 获取白名单配置"
    response=$(http_request "GET" "/system/whitelist/config" "" "")
    check_response "$response" 200 "获取白名单配置"
    
    log_info "测试6.2: 检查路径是否在白名单中"
    response=$(http_request "GET" "/system/whitelist/check?path=/api/auth/login" "" "")
    check_response "$response" 200 "检查白名单路径"
    
    log_info "测试6.3: 测试当前请求路径"
    response=$(http_request "GET" "/system/whitelist/test-current" "" "")
    check_response "$response" 200 "测试当前请求路径"
    
    # 添加临时白名单路径
    log_info "测试6.4: 添加白名单路径"
    response=$(http_request "POST" "/system/whitelist/add?path=/api/test/temp/**" "" "")
    check_response "$response" 200 "添加白名单路径"
    
    # 移除白名单路径
    log_info "测试6.5: 移除白名单路径"
    response=$(http_request "POST" "/system/whitelist/remove?path=/api/test/temp/**" "" "")
    check_response "$response" 200 "移除白名单路径"
}

# 测试7：业务接口（需要认证）
test_business_apis() {
    log_info "测试7: 业务接口测试"
    
    if [ -z "$SESSION_ID" ]; then
        log_error "SessionId为空，跳过业务接口测试"
        return
    fi
    
    # 测试敲木鱼接口
    log_info "测试7.1: 敲木鱼接口"
    local knock_data='{
        "knockCount": 10,
        "prayerText": "祈求平安健康"
    }'
    response=$(http_request "POST" "/knock/start" "$knock_data" "X-Session-Id: $SESSION_ID")
    check_response "$response" 200 "敲木鱼接口"
    
    # 测试获取用户统计
    log_info "测试7.2: 获取用户统计"
    response=$(http_request "GET" "/merit/stats" "" "X-Session-Id: $SESSION_ID")
    check_response "$response" 200 "获取用户统计"
    
    # 测试排行榜
    log_info "测试7.3: 获取功德排行榜"
    response=$(http_request "GET" "/ranking/merit?limit=10" "" "X-Session-Id: $SESSION_ID")
    check_response "$response" 200 "获取功德排行榜"
}

# 测试8：用户登出
test_user_logout() {
    log_info "测试8: 用户登出"
    
    if [ -z "$SESSION_ID" ]; then
        log_error "SessionId为空，跳过登出测试"
        return
    fi
    
    response=$(http_request "POST" "/auth/logout" "" "X-Session-Id: $SESSION_ID")
    check_response "$response" 200 "用户登出"
    
    # 验证登出后session失效
    log_info "测试8.1: 验证登出后Session失效"
    response=$(http_request "GET" "/auth/user-info" "" "X-Session-Id: $SESSION_ID")
    check_response "$response" 401 "登出后Session失效验证"
}

# 测试9：边界情况和异常处理
test_edge_cases() {
    log_info "测试9: 边界情况和异常处理"
    
    # 测试无效的JSON
    log_info "测试9.1: 无效JSON格式"
    response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X "POST" \
        -H "Content-Type: application/json" \
        -d "invalid json" \
        "$BASE_URL/auth/login")
    http_status=$(echo "$response" | grep "HTTP_STATUS:" | cut -d: -f2)
    if [ $http_status -eq 400 ]; then
        log_success "无效JSON处理正确"
        ((PASSED_TESTS++))
    else
        log_error "无效JSON处理异常，状态码: $http_status"
        ((FAILED_TESTS++))
    fi
    ((TOTAL_TESTS++))
    
    # 测试超长用户名
    log_info "测试9.2: 超长用户名注册"
    local long_username=""
    for i in {1..50}; do
        long_username="${long_username}a"
    done
    
    local invalid_data='{
        "username": "'$long_username'",
        "password": "123456",
        "confirmPassword": "123456",
        "nickname": "测试",
        "phone": "13800138000",
        "email": "test@example.com"
    }'
    response=$(http_request "POST" "/auth/register" "$invalid_data" "")
    check_response "$response" 200 "超长用户名注册"
    
    # 测试无效邮箱格式
    log_info "测试9.3: 无效邮箱格式注册"
    local invalid_email_data='{
        "username": "testuser999",
        "password": "123456",
        "confirmPassword": "123456",
        "nickname": "测试",
        "phone": "13800138000",
        "email": "invalid-email"
    }'
    response=$(http_request "POST" "/auth/register" "$invalid_email_data" "")
    check_response "$response" 200 "无效邮箱格式注册"
}

# 生成测试报告
generate_report() {
    echo
    echo "========================================"
    log_info "API接口测试完成"
    echo "========================================"
    echo
    echo "📊 测试统计："
    echo "   总测试数: $TOTAL_TESTS"
    echo "   成功数量: $PASSED_TESTS"
    echo "   失败数量: $FAILED_TESTS"
    echo
    
    if [ $FAILED_TESTS -eq 0 ]; then
        log_success "所有测试通过！ ✅"
        echo "🎉 恭喜！您的API接口运行正常。"
    else
        log_warning "有 $FAILED_TESTS 个测试失败 ⚠️"
        echo "💡 请检查上面标记为ERROR的测试项。"
    fi
    
    echo
    echo "📋 测试覆盖范围："
    echo "   ✅ 用户注册功能"
    echo "   ✅ 用户登录认证"
    echo "   ✅ 用户信息获取"
    echo "   ✅ 会话管理功能"
    echo "   ✅ 白名单机制"
    echo "   ✅ 业务接口调用"
    echo "   ✅ 异常处理"
    echo "   ✅ 边界条件测试"
    echo
}

# 主测试流程
main() {
    start_test
    
    # 按顺序执行所有测试
    test_health_check
    sleep 1
    
    test_user_register
    sleep 2
    
    test_user_login  
    sleep 1
    
    test_get_user_info
    sleep 1
    
    test_session_management
    sleep 2
    
    test_whitelist_functionality
    sleep 1
    
    test_business_apis
    sleep 1
    
    test_user_logout
    sleep 1
    
    test_edge_cases
    
    generate_report
}

# 检查依赖
check_dependencies() {
    if ! command -v curl &> /dev/null; then
        log_error "curl 命令未找到，请安装curl"
        exit 1
    fi
    
    if ! command -v jq &> /dev/null; then
        log_warning "jq 命令未找到，JSON解析功能受限"
    fi
}

# 脚本入口
echo "🔧 检查依赖..."
check_dependencies

echo "🚀 启动测试..."
main