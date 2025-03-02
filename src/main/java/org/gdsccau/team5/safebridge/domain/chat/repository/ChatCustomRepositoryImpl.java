package org.gdsccau.team5.safebridge.domain.chat.repository;

import static org.gdsccau.team5.safebridge.domain.chat.entity.QChat.chat;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.gdsccau.team5.safebridge.common.term.Language;
import org.gdsccau.team5.safebridge.domain.chat.dto.response.ChatResponseDto.ChatMessageWithIsReadResponseDto;
import org.gdsccau.team5.safebridge.domain.chat.entity.QChat;
import org.gdsccau.team5.safebridge.domain.translation.entity.QTranslation;
import org.gdsccau.team5.safebridge.domain.user.entity.QUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
public class ChatCustomRepositoryImpl implements ChatCustomRepository {

    private final JPAQueryFactory queryFactory;

    public ChatCustomRepositoryImpl(final EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Slice<ChatMessageWithIsReadResponseDto> findAllChatsByTeamId(final Long cursorId,
                                                                        final Long teamId,
                                                                        final Language language,
                                                                        final Pageable pageable) {
        QChat chat = QChat.chat;
        QUser user = QUser.user;
        QTranslation translation = QTranslation.translation;

        List<ChatMessageWithIsReadResponseDto> results = queryFactory
                .select(Projections.constructor(
                        ChatMessageWithIsReadResponseDto.class,
                        chat.id,
                        user.name,
                        chat.text,
                        translation.text,
                        ConstantImpl.create(false),
                        chat.createdAt
                ))
                .from(chat)
                .join(user).on(user.id.eq(chat.user.id))
                .join(translation).on(translation.chat.id.eq(chat.id)).on(translation.language.eq(language))
                .where(
                        chat.team.id.eq(teamId)
                                .and(eqCursorId(cursorId))
                )
                .orderBy(chat.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if (results.size() > pageable.getPageSize()) {
            results.remove(pageable.getPageSize());
            hasNext = true;
        }
        return new SliceImpl<>(results, pageable, hasNext);
    }

    private BooleanExpression eqCursorId(final Long cursorId) {
        if (cursorId != 0L) {
            return chat.id.lt(cursorId);
        }
        return null;
    }
}
