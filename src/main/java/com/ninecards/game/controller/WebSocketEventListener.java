package com.ninecards.game.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import com.ninecards.game.service.RoomManager;

@Component
public class WebSocketEventListener {

    @Autowired
    private RoomManager roomManager;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleSubscribe(SessionSubscribeEvent event) {
        String sessionId = (String) event.getMessage().getHeaders().get("simpSessionId");
        String destination = (String) event.getMessage().getHeaders().get("simpDestination");

        if (destination != null && destination.startsWith("/topic/room/")) {
            String roomCode = destination.replace("/topic/room/", "");
            if (!roomCode.contains("/")) { // ignore player-specific topics
                roomManager.registerSession(sessionId, roomCode);
            }
        }
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        String roomCode = roomManager.getRoomCodeBySessionId(sessionId);
        if (roomCode == null) return;

        roomManager.unregisterSession(sessionId);
        roomManager.decrementPlayerCount(roomCode);

        if (!roomManager.isGameStarted(roomCode)) {
            // Game never started — just delete the room silently
            roomManager.deleteRoom(roomCode);
        } else if (roomManager.getConnectedPlayerCount(roomCode) == 0) {
            // Game was running but everyone left
            messagingTemplate.convertAndSend("/topic/room/" + roomCode,
                (Object) Map.of("event", "ROOM_CLOSED", "reason", "All players disconnected"));
            roomManager.deleteRoom(roomCode);
        }
    }
}
