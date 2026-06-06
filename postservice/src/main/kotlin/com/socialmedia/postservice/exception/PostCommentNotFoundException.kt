package com.socialmedia.postservice.exception

import java.util.UUID

class PostCommentNotFoundException(commentId: UUID) : RuntimeException("Comment with id '$commentId' was not found")
