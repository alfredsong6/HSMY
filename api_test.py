#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
敲敲木鱼项目API接口测试脚本
模拟用户注册、登录及各种用户行为
"""

import requests
import json
import time
import random
import string
from datetime import datetime
from typing import Dict, Any, Optional
import sys

class APITester:
    def __init__(self, base_url: str = "http://localhost:8080/api"):
        self.base_url = base_url
        self.session_id = None
        self.user_id = None
        self.username = None
        
        # 测试统计
        self.total_tests = 0
        self.passed_tests = 0
        self.failed_tests = 0
        
        # 创建session对象复用连接
        self.session = requests.Session()
        self.session.headers.update({
            'Content-Type': 'application/json',
            'User-Agent': 'HSMY-API-Tester/1.0'
        })
    
    def log_info(self, message: str):
        """信息日志"""
        print(f"\033[0;34m[INFO]\033[0m {message}")
    
    def log_success(self, message: str):
        """成功日志"""
        print(f"\033[0;32m[SUCCESS]\033[0m {message}")
        self.passed_tests += 1
    
    def log_error(self, message: str):
        """错误日志"""
        print(f"\033[0;31m[ERROR]\033[0m {message}")
        self.failed_tests += 1
    
    def log_warning(self, message: str):
        """警告日志"""
        print(f"\033[1;33m[WARNING]\033[0m {message}")
    
    def make_request(self, method: str, endpoint: str, data: Dict[Any, Any] = None, 
                    headers: Dict[str, str] = None, expected_status: int = 200) -> Optional[Dict]:
        """发送HTTP请求"""
        self.total_tests += 1
        url = f"{self.base_url}{endpoint}"
        
        try:
            # 准备headers
            req_headers = {}
            if headers:
                req_headers.update(headers)
            if self.session_id and 'X-Session-Id' not in req_headers:
                req_headers['X-Session-Id'] = self.session_id
            
            # 发送请求
            if method.upper() == 'GET':
                response = self.session.get(url, headers=req_headers, timeout=10)
            elif method.upper() == 'POST':
                response = self.session.post(url, json=data, headers=req_headers, timeout=10)
            elif method.upper() == 'PUT':
                response = self.session.put(url, json=data, headers=req_headers, timeout=10)
            elif method.upper() == 'DELETE':
                response = self.session.delete(url, headers=req_headers, timeout=10)
            else:
                raise ValueError(f"不支持的HTTP方法: {method}")
            
            # 检查状态码
            if response.status_code == expected_status:
                self.log_success(f"{method} {endpoint} - 状态码: {response.status_code}")
            else:
                self.log_error(f"{method} {endpoint} - 期望状态码: {expected_status}, 实际: {response.status_code}")
            
            # 尝试解析JSON响应
            try:
                result = response.json()
                print(json.dumps(result, ensure_ascii=False, indent=2))
                return result
            except ValueError:
                print(f"响应内容: {response.text}")
                return {"raw_response": response.text}
                
        except requests.exceptions.RequestException as e:
            self.log_error(f"请求失败: {e}")
            return None
        finally:
            print("-" * 50)
    
    def generate_random_user(self) -> Dict[str, str]:
        """生成随机用户数据"""
        timestamp = int(time.time())
        random_suffix = ''.join(random.choices(string.digits, k=4))
        
        username = f"testuser_{timestamp}_{random_suffix}"
        return {
            "username": username,
            "password": "123456",
            "confirmPassword": "123456", 
            "nickname": f"测试用户_{timestamp}",
            "phone": f"138{random.randint(10000000, 99999999)}",
            "email": f"{username}@example.com"
        }
    
    def test_health_check(self):
        """测试1: 健康检查"""
        self.log_info("测试1: 健康检查接口")
        result = self.make_request("GET", "/auth/health")
        return result
    
    def test_user_register(self):
        """测试2: 用户注册"""
        self.log_info("测试2: 用户注册")
        
        # 生成随机用户
        user_data = self.generate_random_user()
        self.username = user_data["username"]
        
        result = self.make_request("POST", "/auth/register", user_data)
        
        # 测试重复注册
        self.log_info("测试2.1: 重复用户名注册（应该失败）")
        self.make_request("POST", "/auth/register", user_data)
        
        # 测试密码不一致
        self.log_info("测试2.2: 密码不一致注册（应该失败）")
        invalid_data = user_data.copy()
        invalid_data["username"] = f"testuser2_{int(time.time())}"
        invalid_data["confirmPassword"] = "different_password"
        self.make_request("POST", "/auth/register", invalid_data)
        
        # 测试无效邮箱
        self.log_info("测试2.3: 无效邮箱格式注册（应该失败）")
        invalid_email_data = user_data.copy()
        invalid_email_data["username"] = f"testuser3_{int(time.time())}"
        invalid_email_data["email"] = "invalid-email-format"
        self.make_request("POST", "/auth/register", invalid_email_data)
        
        return result
    
    def test_user_login(self):
        """测试3: 用户登录"""
        self.log_info("测试3: 用户登录")
        
        if not self.username:
            self.log_error("用户名为空，跳过登录测试")
            return None
        
        login_data = {
            "loginAccount": self.username,
            "password": "123456"
        }
        
        result = self.make_request("POST", "/auth/login", login_data)
        
        # 提取session信息
        if result and result.get("code") == 200 and "data" in result:
            self.session_id = result["data"].get("sessionId")
            self.user_id = result["data"].get("userId")
            if self.session_id:
                self.log_success(f"获取到SessionId: {self.session_id}")
                self.log_success(f"获取到UserId: {self.user_id}")
        
        # 测试错误密码
        self.log_info("测试3.1: 错误密码登录（应该失败）")
        wrong_data = login_data.copy()
        wrong_data["password"] = "wrongpassword"
        self.make_request("POST", "/auth/login", wrong_data)
        
        # 测试不存在用户
        self.log_info("测试3.2: 不存在用户登录（应该失败）")
        nonexistent_data = {
            "loginAccount": "nonexistentuser",
            "password": "123456"
        }
        self.make_request("POST", "/auth/login", nonexistent_data)
        
        return result
    
    def test_get_user_info(self):
        """测试4: 获取用户信息"""
        self.log_info("测试4: 获取当前用户信息")
        
        if not self.session_id:
            self.log_error("SessionId为空，跳过需要认证的测试")
            return None
        
        result = self.make_request("GET", "/auth/user-info")
        
        # 测试无效SessionId
        self.log_info("测试4.1: 无效SessionId获取用户信息（应该失败）")
        self.make_request("GET", "/auth/user-info", 
                         headers={"X-Session-Id": "invalid_session"}, 
                         expected_status=401)
        
        # 测试无SessionId
        self.log_info("测试4.2: 无SessionId获取用户信息（应该失败）")
        self.make_request("GET", "/auth/user-info", 
                         headers={}, 
                         expected_status=401)
        
        return result
    
    def test_session_management(self):
        """测试5: 会话管理"""
        self.log_info("测试5: 会话管理")
        
        if not self.session_id:
            self.log_error("SessionId为空，跳过会话管理测试")
            return None
        
        # 获取用户所有会话
        self.log_info("测试5.1: 获取用户所有会话")
        result = self.make_request("GET", "/auth/sessions")
        
        # 模拟多端登录
        self.log_info("测试5.2: 模拟多端登录")
        login_data = {
            "loginAccount": self.username,
            "password": "123456"
        }
        login_result = self.make_request("POST", "/auth/login", login_data, headers={})
        
        new_session_id = None
        if login_result and login_result.get("code") == 200:
            new_session_id = login_result["data"].get("sessionId")
            if new_session_id:
                self.log_success(f"获取到新的SessionId: {new_session_id}")
        
        # 查看多端登录后的会话列表
        self.log_info("测试5.3: 多端登录后的会话列表")
        self.make_request("GET", "/auth/sessions")
        
        # 踢出其他会话
        self.log_info("测试5.4: 踢出其他登录会话")
        self.make_request("POST", "/auth/kick-other-sessions")
        
        # 验证新sessionId已失效
        if new_session_id:
            self.log_info("测试5.5: 验证被踢出的会话已失效")
            self.make_request("GET", "/auth/user-info", 
                             headers={"X-Session-Id": new_session_id}, 
                             expected_status=401)
        
        return result
    
    def test_whitelist_functionality(self):
        """测试6: 白名单功能"""
        self.log_info("测试6: 白名单功能测试")
        
        # 测试获取白名单配置（不需要登录）
        self.log_info("测试6.1: 获取白名单配置")
        self.make_request("GET", "/system/whitelist/config", headers={})
        
        # 测试检查路径
        self.log_info("测试6.2: 检查路径是否在白名单中")
        self.make_request("GET", "/system/whitelist/check?path=/api/auth/login", headers={})
        
        # 测试当前请求路径
        self.log_info("测试6.3: 测试当前请求路径")
        self.make_request("GET", "/system/whitelist/test-current", headers={})
        
        # 添加临时白名单路径
        self.log_info("测试6.4: 添加白名单路径")
        self.make_request("POST", "/system/whitelist/add?path=/api/test/temp/**", headers={})
        
        # 移除白名单路径
        self.log_info("测试6.5: 移除白名单路径")
        self.make_request("POST", "/system/whitelist/remove?path=/api/test/temp/**", headers={})
    
    def test_business_apis(self):
        """测试7: 业务接口"""
        self.log_info("测试7: 业务接口测试")
        
        if not self.session_id:
            self.log_error("SessionId为空，跳过业务接口测试")
            return
        
        # 测试敲木鱼接口
        self.log_info("测试7.1: 敲木鱼接口")
        knock_data = {
            "knockCount": 10,
            "prayerText": "祈求平安健康"
        }
        self.make_request("POST", "/knock/start", knock_data)
        
        # 测试获取用户统计
        self.log_info("测试7.2: 获取用户统计")
        self.make_request("GET", "/merit/stats")
        
        # 测试排行榜
        self.log_info("测试7.3: 获取功德排行榜")
        self.make_request("GET", "/ranking/merit?limit=10")
        
        # 测试用户设置
        self.log_info("测试7.4: 获取用户设置")
        self.make_request("GET", "/user/settings")
    
    def test_user_logout(self):
        """测试8: 用户登出"""
        self.log_info("测试8: 用户登出")
        
        if not self.session_id:
            self.log_error("SessionId为空，跳过登出测试")
            return
        
        result = self.make_request("POST", "/auth/logout")
        
        # 验证登出后session失效
        self.log_info("测试8.1: 验证登出后Session失效")
        self.make_request("GET", "/auth/user-info", expected_status=401)
        
        # 清空session信息
        self.session_id = None
        self.user_id = None
        
        return result
    
    def test_edge_cases(self):
        """测试9: 边界情况和异常处理"""
        self.log_info("测试9: 边界情况和异常处理")
        
        # 测试超长用户名
        self.log_info("测试9.1: 超长用户名注册")
        long_username_data = {
            "username": "a" * 50,  # 超过20位限制
            "password": "123456",
            "confirmPassword": "123456",
            "nickname": "测试用户",
            "phone": "13800138000",
            "email": "test@example.com"
        }
        self.make_request("POST", "/auth/register", long_username_data)
        
        # 测试短密码
        self.log_info("测试9.2: 密码过短注册")
        short_password_data = {
            "username": f"testuser_{int(time.time())}",
            "password": "123",  # 少于6位
            "confirmPassword": "123",
            "nickname": "测试用户",
            "phone": "13800138000",
            "email": "test2@example.com"
        }
        self.make_request("POST", "/auth/register", short_password_data)
        
        # 测试无效手机号
        self.log_info("测试9.3: 无效手机号格式注册")
        invalid_phone_data = {
            "username": f"testuser_{int(time.time())}_2",
            "password": "123456",
            "confirmPassword": "123456",
            "nickname": "测试用户",
            "phone": "12345",  # 无效手机号
            "email": "test3@example.com"
        }
        self.make_request("POST", "/auth/register", invalid_phone_data)
    
    def test_concurrent_users(self):
        """测试10: 模拟多用户并发"""
        self.log_info("测试10: 模拟多用户并发测试")
        
        users = []
        for i in range(3):
            self.log_info(f"创建并发用户 {i+1}")
            user_data = self.generate_random_user()
            
            # 注册用户
            register_result = self.make_request("POST", "/auth/register", user_data, headers={})
            
            # 登录用户
            login_data = {
                "loginAccount": user_data["username"],
                "password": user_data["password"]
            }
            login_result = self.make_request("POST", "/auth/login", login_data, headers={})
            
            if login_result and login_result.get("code") == 200:
                session_id = login_result["data"].get("sessionId")
                users.append({
                    "username": user_data["username"],
                    "session_id": session_id
                })
                
                # 获取用户信息
                self.make_request("GET", "/auth/user-info", 
                                headers={"X-Session-Id": session_id})
            
            time.sleep(0.5)
        
        # 清理测试用户
        for user in users:
            if user["session_id"]:
                self.make_request("POST", "/auth/logout", 
                                headers={"X-Session-Id": user["session_id"]})
    
    def run_all_tests(self):
        """运行所有测试"""
        print("=" * 60)
        self.log_info("开始敲敲木鱼项目API接口测试")
        print("=" * 60)
        print()
        
        start_time = time.time()
        
        try:
            # 按顺序执行所有测试
            self.test_health_check()
            time.sleep(1)
            
            self.test_user_register()
            time.sleep(2)
            
            self.test_user_login()
            time.sleep(1)
            
            self.test_get_user_info()
            time.sleep(1)
            
            self.test_session_management()
            time.sleep(2)
            
            self.test_whitelist_functionality()
            time.sleep(1)
            
            self.test_business_apis()
            time.sleep(1)
            
            self.test_user_logout()
            time.sleep(1)
            
            self.test_edge_cases()
            time.sleep(1)
            
            self.test_concurrent_users()
            
        except KeyboardInterrupt:
            self.log_warning("测试被用户中断")
        except Exception as e:
            self.log_error(f"测试过程中发生异常: {e}")
        finally:
            # 生成测试报告
            self.generate_report(time.time() - start_time)
    
    def generate_report(self, duration: float):
        """生成测试报告"""
        print()
        print("=" * 60)
        self.log_info("API接口测试完成")
        print("=" * 60)
        print()
        
        print("📊 测试统计：")
        print(f"   总测试数: {self.total_tests}")
        print(f"   成功数量: {self.passed_tests}")
        print(f"   失败数量: {self.failed_tests}")
        print(f"   成功率: {self.passed_tests/self.total_tests*100:.1f}%" if self.total_tests > 0 else "   成功率: N/A")
        print(f"   测试用时: {duration:.2f}秒")
        print()
        
        if self.failed_tests == 0:
            self.log_success("所有测试通过！ ✅")
            print("🎉 恭喜！您的API接口运行正常。")
        else:
            self.log_warning(f"有 {self.failed_tests} 个测试失败 ⚠️")
            print("💡 请检查上面标记为ERROR的测试项。")
        
        print()
        print("📋 测试覆盖范围：")
        test_coverage = [
            "✅ 健康检查",
            "✅ 用户注册功能",
            "✅ 用户登录认证", 
            "✅ 用户信息获取",
            "✅ 会话管理功能",
            "✅ 白名单机制",
            "✅ 业务接口调用",
            "✅ 用户登出",
            "✅ 边界条件测试",
            "✅ 并发用户测试"
        ]
        
        for item in test_coverage:
            print(f"   {item}")
        print()
        
        print("📝 建议：")
        if self.failed_tests > 0:
            print("   1. 检查服务器是否正常启动（端口8080）")
            print("   2. 确认数据库连接正常")
            print("   3. 检查Redis服务是否运行")
            print("   4. 查看服务器日志排查具体错误")
        else:
            print("   1. 可以进行更多业务场景测试")
            print("   2. 建议在不同环境下重复测试")
            print("   3. 考虑添加性能和压力测试")
        print()


def main():
    """主函数"""
    import argparse
    
    parser = argparse.ArgumentParser(description='敲敲木鱼项目API接口测试')
    parser.add_argument('--url', default='http://localhost:8080/api', 
                       help='API基础URL (默认: http://localhost:8080/api)')
    parser.add_argument('--verbose', action='store_true', 
                       help='显示详细输出')
    
    args = parser.parse_args()
    
    # 创建测试器实例
    tester = APITester(args.url)
    
    # 运行测试
    tester.run_all_tests()


if __name__ == "__main__":
    main()