package jp.co.myogadanimotors.yushima.publisher;

import jp.co.myogadanimotors.bunkyo.config.ConfigAccessor;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class JmsMarketDataPublisherFactory implements IMarketDataPublisherFactory {

    private TopicConnection connection;
    private TopicSession session;

    public void init(ConfigAccessor configAccessor) throws MarketDataPublisherException {
        String brokerUrl = configAccessor.getString("jms.brokerUrl");
        TopicConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);

        try {
            // create connection
            connection = factory.createTopicConnection();
            connection.start();

            // create session
            session = connection.createTopicSession(false, QueueSession.AUTO_ACKNOWLEDGE);
        } catch (JMSException e) {
            throw new MarketDataPublisherException("exception on initialization.", e);
        }
    }

    public void close() throws MarketDataPublisherException {
        try {
            if (session != null) session.close();
            if (connection != null) connection.close();
        } catch (JMSException e) {
            throw new MarketDataPublisherException("exception on closing.", e);
        }
    }

    @Override
    public IMarketDataPublisher create(String symbol) throws MarketDataPublisherException {
        TopicPublisher publisher;
        try {
            publisher = session.createPublisher(session.createTopic(symbol));
        } catch (JMSException e) {
            throw new MarketDataPublisherException("exception on creating publisher. (symbol: " + symbol + ")", e);
        }
        return new JmsMarketDataPublisher(session, publisher);
    }
}
