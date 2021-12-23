/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.graphql.data.method.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Annotation for a handler method that batch loads field values, given a list
 * of source/parent values. For example:
 *
 * <pre class="code">
 * &#064;BatchMapping
 * public Mono&lt;Map&lt;Book, Author&gt;&gt; author(List&lt;Book&gt; books) {
 *     // ...
 * }
 * </pre>
 *
 * <p>The annotated method is registered as a batch loading function via
 * {@link org.springframework.graphql.execution.BatchLoaderRegistry}, and along
 * with it, a {@link graphql.schema.DataFetcher} for the field is registered
 * transparently that looks up the field value through the registered
 * {@code DataLoader}.
 *
 * <p>Effectively, a shortcut for:
 *
 * <pre class="code">
 * &#064;Controller
 * public class BookController {
 *
 *     public BookController(BatchLoaderRegistry registry) {
 *         registry.forTypePair(Long.class, Author.class).registerMappedBatchLoader((ids, environment) -> ...);
 *     }
 *
 *     &#064;SchemaMapping
 *     public CompletableFuture&lt;Author&gt; author(Book book, DataLoader&lt;Long, Author&gt; dataLoader) {
 *         return dataLoader.load(book.getAuthorId());
 *     }
 *
 * }
 * </pre>
 *
 * <p>In addition to returning {@code Mono<Map<K,T>>}, an {@code @BatchMapping}
 * method can also return {@code Flux<T>}. However, in that case the returned
 * sequence of values must match the number and order of the input keys. See
 * {@link org.dataloader.BatchLoader} and {@link org.dataloader.MappedBatchLoader}
 * for more details.
 *
 * @author Rossen Stoyanchev
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BatchMapping {

	/**
	 * Customize the name of the GraphQL field to bind to.
	 * <p>By default, if not specified, this is initialized from the method name.
	 */
	@AliasFor("value")
	String field() default "";

	/**
	 * Effectively an alias for {@link #field()}.
	 */
	@AliasFor("field")
	String value() default "";

	/**
	 * Customizes the name of the source/parent type for the GraphQL field.
	 * <p>By default, if not specified, it is based on the simple class name of
	 * the List of source/parent values injected into the handler method.
	 * <p>The value for this attribute can also be inherited from a class-level
	 * {@link SchemaMapping @SchemaMapping}. When used on both levels, the one
	 * here overrides the one at the class level.
	 */
	String[] typeNames() default {};

}
