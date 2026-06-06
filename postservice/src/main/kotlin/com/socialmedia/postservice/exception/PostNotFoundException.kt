package com.socialmedia.postservice.exception

import java.util.UUID

class PostNotFoundException(postId: UUID) : RuntimeException("Post with id '$postId' was not found")
