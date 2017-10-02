# NettyMqService
A simple project demonstrates how to use Netty with RabbitMQ.  
中文详情：[Chinese Details](http://www.cnblogs.com/luxiaoxun/p/4257105.html)
### Design:
![design](http://images.cnitblog.com/blog/434101/201501/282041130971204.jpg)
### Features:
* Receive TCP packets from different clients and decode the message.
* Send the TCP message to the third parts with rabbit mq.
* Consume rabbit mq messages and send the message to TCP clients.
