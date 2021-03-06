package com.bookstore.service;

import java.util.List;
import java.util.Optional;

import com.bookstore.domain.Book;

public interface BookService {
	List<Book> findAll ();
	
	Optional<Book> findOne(Long id);

	List<Book> findByCategory(String category);

	List<Book> blurrySearch(String keyword);
}
