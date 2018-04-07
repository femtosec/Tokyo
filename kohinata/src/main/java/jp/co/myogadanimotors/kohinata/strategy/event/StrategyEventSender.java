package jp.co.myogadanimotors.kohinata.strategy.event;

import jp.co.myogadanimotors.bunkyo.eventprocessing.BaseEventSender;
import jp.co.myogadanimotors.bunkyo.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.bunkyo.timesource.ITimeSource;
import jp.co.myogadanimotors.kohinata.event.order.OrderDestination;
import jp.co.myogadanimotors.kohinata.event.order.Orderer;
import jp.co.myogadanimotors.kohinata.strategy.context.OrderView;
import jp.co.myogadanimotors.kohinata.strategy.event.childorder.*;
import jp.co.myogadanimotors.kohinata.strategy.event.childorderfill.StrategyChildOrderFill;
import jp.co.myogadanimotors.kohinata.strategy.event.order.*;
import jp.co.myogadanimotors.kohinata.strategy.event.timer.StrategyTimerEvent;

import java.math.BigDecimal;

public class StrategyEventSender extends BaseEventSender<IStrategyEventListener> {

    public StrategyEventSender(EventIdGenerator idGenerator, ITimeSource timeSource) {
        super(idGenerator, timeSource);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // order event senders
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public void sendStrategyNew(long requestId,
                                OrderView orderView,
                                Orderer orderer,
                                OrderDestination destination) {
        send((eventId, creationTime, asyncEventListener) ->
                new StrategyNew(
                        eventId,
                        creationTime,
                        asyncEventListener,
                        requestId,
                        orderView,
                        orderer,
                        destination
                )
        );
    }

    public void sendStrategyAmend(long requestId,
                                  OrderView orderView,
                                  OrderView amendOrderView,
                                  Orderer orderer,
                                  OrderDestination destination) {
        send((eventId, creationTime, asyncEventListener) ->
                new StrategyAmend(
                        eventId,
                        creationTime,
                        asyncEventListener,
                        requestId,
                        orderView,
                        amendOrderView,
                        orderer,
                        destination
                )
        );
    }

    public void sendStrategyCancel(long requestId,
                                   OrderView orderView,
                                   Orderer orderer,
                                   OrderDestination destination) {
        send((eventId, creationTime, asyncEventListener) ->
                new StrategyCancel(
                        eventId,
                        creationTime,
                        asyncEventListener,
                        requestId,
                        orderView,
                        orderer,
                        destination
                )
        );
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // report event senders
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public void sendStrategyNewAck(OrderView orderView) {
        send((eventId, creationTime, asyncEventListener) ->
                new StrategyNewAck(
                        eventId,
                        creationTime,
                        asyncEventListener,
                        orderView
                )
        );
    }

    public void sendStrategyAmendAck(OrderView orderView) {
        send((eventId, creationTime, asyncEventListener) ->
                new StrategyAmendAck(
                        eventId,
                        creationTime,
                        asyncEventListener,
                        orderView
                )
        );
    }

    public void sendStrategyCancelAck(OrderView orderView) {
        send((eventId, creationTime, asyncEventListener) ->
                new StrategyCancelAck(
                        eventId,
                        creationTime,
                        asyncEventListener,
                        orderView
                )
        );
    }

    public void sendStrategyNewReject(OrderView orderView, String message) {
        send((eventId, creationTime, asyncEventListener) ->
                new StrategyNewReject(
                        eventId,
                        creationTime,
                        asyncEventListener,
                        orderView,
                        message
                )
        );
    }

    public void sendStrategyAmendReject(OrderView orderView, String message) {
        send((eventId, creationTime, asyncEventListener) ->
                new StrategyAmendReject(
                        eventId,
                        creationTime,
                        asyncEventListener,
                        orderView,
                        message
                )
        );
    }

    public void sendStrategyCancelReject(OrderView orderView, String message) {
        send((eventId, creationTime, asyncEventListener) ->
                new StrategyCancelReject(
                        eventId,
                        creationTime,
                        asyncEventListener,
                        orderView,
                        message
                )
        );
    }

    public void sendStrategyUnsolicitedCancel(OrderView orderView, String message) {
        send((eventId, creationTime, asyncEventListener) ->
                new StrategyUnsolicitedCancel(
                        eventId,
                        creationTime,
                        asyncEventListener,
                        orderView,
                        message
                )
        );
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // child order report senders
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public void sendStrategyChildOrderNewAck(OrderView orderView,
                                             OrderView childOrderView,
                                             String childOrderTag) {
        send((eventId, creationTime, asyncEventListener) ->
                new StrategyChildOrderNewAck(
                        eventId,
                        creationTime,
                        asyncEventListener,
                        orderView,
                        childOrderView,
                        childOrderTag
                )
        );
    }

    public void sendStrategyChildOrderAmendAck(OrderView orderView,
                                               OrderView childOrderView,
                                               String childOrderTag) {
        send((eventId, creationTime, asyncEventListener) ->
                new StrategyChildOrderAmendAck(
                        eventId,
                        creationTime,
                        asyncEventListener,
                        orderView,
                        childOrderView,
                        childOrderTag
                )
        );
    }

    public void sendStrategyChildOrderCancelAck(OrderView orderView,
                                                OrderView childOrderView,
                                                String childOrderTag) {
        send((eventId, creationTime, asyncEventListener) ->
                new StrategyChildOrderCancelAck(
                        eventId,
                        creationTime,
                        asyncEventListener,
                        orderView,
                        childOrderView,
                        childOrderTag
                )
        );
    }

    public void sendStrategyChildOrderNewReject(OrderView orderView,
                                                OrderView childOrderView,
                                                String childOrderTag,
                                                String message) {
        send((eventId, creationTime, asyncEventListener) ->
                new StrategyChildOrderNewReject(
                        eventId,
                        creationTime,
                        asyncEventListener,
                        orderView,
                        childOrderView,
                        childOrderTag,
                        message
                )
        );
    }

    public void sendStrategyChildOrderAmendReject(OrderView orderView,
                                                  OrderView childOrderView,
                                                  String childOrderTag,
                                                  String message) {
        send((eventId, creationTime, asyncEventListener) ->
                new StrategyChildOrderAmendReject(
                        eventId,
                        creationTime,
                        asyncEventListener,
                        orderView,
                        childOrderView,
                        childOrderTag,
                        message
                )
        );
    }

    public void sendStrategyChildOrderCancelReject(OrderView orderView,
                                                   OrderView childOrderView,
                                                   String childOrderTag,
                                                   String message) {
        send((eventId, creationTime, asyncEventListener) ->
                new StrategyChildOrderCancelReject(
                        eventId,
                        creationTime,
                        asyncEventListener,
                        orderView,
                        childOrderView,
                        childOrderTag,
                        message
                )
        );
    }

    public void sendStrategyChildOrderUnsolicitedCancel(OrderView orderView,
                                                        OrderView childOrderView,
                                                        String childOrderTag,
                                                        String message) {
        send((eventId, creationTime, asyncEventListener) ->
                new StrategyChildOrderUnsolicitedCancel(
                        eventId,
                        creationTime,
                        asyncEventListener,
                        orderView,
                        childOrderView,
                        childOrderTag,
                        message
                )
        );
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // child order fill senders
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public void sendStrategyChildOrderFill(BigDecimal execQuantity,
                                           OrderView orderView,
                                           OrderView childOrderView,
                                           String childOrderTag) {
        send((eventId, creationTime, asyncEventListener) ->
                new StrategyChildOrderFill(
                        eventId,
                        creationTime,
                        asyncEventListener,
                        execQuantity,
                        orderView,
                        childOrderView,
                        childOrderTag
                )
        );
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // timer event senders
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public void sendStrategyTimerEvent(long timerTag,
                                       long timerEventTime) {
        send((eventId, creationTime, asyncEventListener) ->
                new StrategyTimerEvent(
                        eventId,
                        creationTime,
                        asyncEventListener,
                        timerTag,
                        timerEventTime
                )
        );
    }
}
