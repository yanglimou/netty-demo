# netty

## 目标

可以做智能硬件设备和第三方java平台的数据透传中间件

## 步骤

- 开启socket服务器，接受智能硬件设备发来的消息，识别后调用第三方java平台的接口，获取响应后将响应传给智能硬件设备（简单）
- 开启http服务器，接受第三方平台的消息，识别第三方平台要调用的智能设备，将消息发送给智能设备，同步等待智能设备回传的消息，把智能设备回传的消息作为这一次请求的响应发送给第三方平台

## 难点

步骤二是难点

根据请求找出具体哪一个硬件设备发送消息，这个好实现，可以在硬件设备发送心跳以后将通信通道绑定到全局map中

请求需要同步等待响应，这个不好处理，因为和硬件设备的通信是异步的，虽然发过去了请求，但是响应回传在别的线程里面

## demo开发

### 1. 三个组件

- 智能设备（client）
- 中间件（server）
- 第三方（thirdpart）

### 2. 各自的功能

- 智能设备（client）

心跳功能：能连接中间件，能定时向中间件发送心跳，接受到心跳回传消息要打印出来

指纹信息配置：能接收中间件传来的指纹配置信息，并返回中间件配置结果

- 中间件（server）

心跳功能：启动socket服务器，接受智能设备发送的心跳信息，并把心跳信息发送给第三方，并将第三方的回传信息返回给智能设备

指纹信息配置：启动http服务器，接受第三方指纹配置请求，根据指纹配置请求找出具体的智能设备，并把信息传递给智能设备，同步等待智能设备的回传信息，并把回传信息响应给第三方

- 第三方（thirdpart）

心跳功能：启动http服务器，接受中间件的心跳信息，并回传响应给中间件

指纹信息配置：发送指纹信息给中间件，并接收配置操作结果

## demo运行

- 中间件（server）

运行ServerApplication.java的main方法，启动http服务（8082）和tcp服务（8081）

- 第三方（thirdpart）

运行ThirdPartApplication.java的main方法，启动http服务（8080）

- 智能设备（client）

NettyClientApplication.java的main方法，启动http服务（8082）

调用http://localhost:8082/createClient?id=1 创建id为1的客户端

调用http://localhost:8082/createClient?id=2 创建id为2的客户端

调用http://localhost:8082/createClient?id=3 创建id为3的客户端

定时任务定时10秒发送一次心跳给中间件并最终传给第三方，并返回结果