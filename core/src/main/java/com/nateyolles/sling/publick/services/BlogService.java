package com.nateyolles.sling.publick.services;

import javax.jcr.NodeIterator;

public interface BlogService {
  NodeIterator getPosts();

  NodeIterator getPosts(Long offset, Long limit);

  long getNumberOfPosts();

  long getNumberOfPages(int pageSize);
}