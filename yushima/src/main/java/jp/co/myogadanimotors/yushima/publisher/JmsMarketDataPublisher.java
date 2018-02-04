package jp.co.myogadanimotors.yushima.publisher;

import jp.co.myogadanimotors.yushima.mdmanager.IMarketDataEntry;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import java.util.Objects;

public class JmsMarketDataPublisher implements IMarketDataPublisher {

    private final TopicSession session;
    private final TopicPublisher publisher;

    public JmsMarketDataPublisher(TopicSession session, TopicPublisher publisher) {
        this.session = Objects.requireNonNull(session);
        this.publisher = Objects.requireNonNull(publisher);
    }

    @Override
    public void publish(IMarketDataEntry marketDataEntry) throws MarketDataPublisherException {
        try {
            Message message = session.createTextMessage(toJsonFormat(marketDataEntry));
            publisher.publish(message);
        } catch (JMSException e) {
            throw new MarketDataPublisherException("exception on publishing.", e);
        }
    }

    @Override
    public void close() throws MarketDataPublisherException {
        try {
            publisher.close();
        } catch (JMSException e) {
            throw new MarketDataPublisherException("exception on closing.", e);
        }
    }

    private String toJsonFormat(IMarketDataEntry marketDataEntry) {
        // todo: to be implemented
        return null;
    }
}
