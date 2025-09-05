五、接口请求返回体规范
1.请求返回提必须包含三个字段：
(1)code：响应状态码
(2)message：响应信息
(3)data：响应数据
2.部分响应状态的定义如下：
(1){Code: 200, message：操作成功, data：具体的响应数据内容}
(2){Code: 401, message：您尚未登录，请先登录, data：null}
(3){Code: 403, message：您无权访问该资源, data：null}
(4){Code: 404, message：抱歉，未找到您请求的页面, data：null}
(5){Code: 500 , message：服务器开小差，请稍后尝试, data：null}