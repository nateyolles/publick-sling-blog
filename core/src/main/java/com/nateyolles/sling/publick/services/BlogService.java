package com.nateyolles.sling.publick.services;

import javax.jcr.NodeIterator;

/**
 * API to search and retrieve blog posts.
 */
public interface BlogService {

    /**
     * Get all blog posts in order of newest first.
     *
     * @return All blog posts in order of newest first.
     */
    NodeIterator getPosts();

    /**
     * Get paginated blog posts in order of newest first.
     *
     * @param offset The starting point of blog posts to get.
     * @param limit The number of blog posts to get.
     * @return The blog posts according to the starting point and length.
     */
    NodeIterator getPosts(Long offset, Long limit);

    /**
     * Get the number of blog posts in the system.
     *
     * @return The number of blog posts.
     */
    long getNumberOfPosts();

    /**
     * Get the number of pagination pages determined by the total
     * number of blog posts and specified number of blog posts
     * per page.
     *
     * @param pageSize The number of blog pages per size.
     * @return The number of pagination pages required to display all
     *            blog posts.
     */
    long getNumberOfPages(int pageSize);
}