package jp.co.myogadanimotors.yushima.subscriber

import org.apache.activemq.ActiveMQConnection
import org.apache.activemq.ActiveMQConnectionFactory
import org.testng.annotations.Test

import javax.jms.*

class JMSTest {
    @Test
    void testTopicSender() {
        TopicConnection connection = null;
        TopicSession session = null;
        TopicPublisher publisher = null;
        try {
            //Connectionを作成
            TopicConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
            connection = factory.createTopicConnection();
            connection.start();

            //Publisherの作成
            session = connection.createTopicSession(false,QueueSession.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic("topic_test");
            publisher = session.createPublisher(topic);

            //メッセージの送信
            TextMessage msg = session.createTextMessage("Hello Message!");
            publisher.publish(msg);
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            try {
                if (publisher != null) publisher.close();
                if (session != null) session.close();
                if (connection != null) connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    void testTopicReceiver() {
        TopicConnection connection = null;
        TopicSession session = null;
        TopicSubscriber subscriber = null;
        try {
            //Connectionを作成
            TopicConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
            connection = factory.createTopicConnection();
            connection.start();

            //Subscriberの作成
            session = connection.createTopicSession(false,TopicSession.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic("topic_test");
            subscriber= session.createSubscriber(topic);

            //メッセージの受信
            TextMessage msg = (TextMessage) subscriber.receive();
            System.out.println(msg.getText());
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            try {
                if (subscriber != null) subscriber.close();
                if (session != null) session.close();
                if (connection != null) connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
