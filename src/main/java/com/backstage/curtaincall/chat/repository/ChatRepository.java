package com.backstage.curtaincall.chat.repository;

import com.backstage.curtaincall.chat.document.Chat;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatRepository extends MongoRepository<Chat, String> {

    @Aggregation(pipeline = {
            "{ $match: { '_id': ?0 } }",
            "{ $unwind: '$messages' }",
            "{ $sort: { 'messages.createAt': 1 } }",
            "{ $skip: ?1 }",
            "{ $limit: ?2 }",
            "{ $group: { '_id': '$_id', 'messages': { $push: '$messages' } } }"
    })
    Chat findAllByIdWithPaging(String roomId, long offset, int limit);

    @Aggregation(pipeline = {
            "{ $match: { '_id': ?0 } }",
            "{ $unwind: '$messages' }",
            "{ $group: { '_id': '$_id', 'count': { $sum: 1 } } }"
    })
    long getTotalCountById(String roomId);
}
