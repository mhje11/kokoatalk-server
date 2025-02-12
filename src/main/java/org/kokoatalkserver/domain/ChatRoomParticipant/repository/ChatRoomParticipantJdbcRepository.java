package org.kokoatalkserver.domain.ChatRoomParticipant.repository;

import lombok.RequiredArgsConstructor;
import org.kokoatalkserver.domain.ChatRoomParticipant.entity.ChatRoomParticipant;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatRoomParticipantJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    public void batchInsertParticipants(List<ChatRoomParticipant> participants) {
        String sql = "INSERT INTO chat_room_participant (chat_room_id, kokoa_id) VALUES (?, ?)";


        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, participants.get(i).getChatRoom().getId());
                ps.setLong(2, participants.get(i).getMember().getKokoaId());
            }

            @Override
            public int getBatchSize() {
                return participants.size();
            }
        });
    }
}
