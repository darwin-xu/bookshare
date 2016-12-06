/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sample.data.rest.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import sample.data.rest.domain.Book;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "page", path = "page")
interface BookRepository extends PagingAndSortingRepository<Book, Long> {
/*
	List<String> getColumnList(@Param("pageName") String pageName);

	List<String> getBookList(@Param("columnName") String columnName);
*/
	Book getBook(@Param("isbn10")String isbn10);

}
