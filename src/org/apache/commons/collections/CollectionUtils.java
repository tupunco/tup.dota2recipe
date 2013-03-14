/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Provides utility methods and decorators for {@link Collection} instances.
 * 
 * @since Commons Collections 1.0
 * @version $Revision: 646777 $ $Date: 2008-04-10 13:33:15 +0100 (Thu, 10 Apr
 *          2008) $
 * 
 * @author Rodney Waldhoff
 * @author Paul Jack
 * @author Stephen Colebourne
 * @author Steve Downey
 * @author Herve Quiroz
 * @author Peter KoBek
 * @author Matthew Hawthorne
 * @author Janek Bogucki
 * @author Phil Steitz
 * @author Steven Melzer
 * @author Jon Schewe
 * @author Neil O'Toole
 * @author Stephen Smith
 */
public final class CollectionUtils {
    /**
     * Finds the first element in the given collection which matches the given
     * predicate.
     * <p>
     * If the input collection or predicate is null, or no element of the
     * collection matches the predicate, null is returned.
     * 
     * @param collection
     *            the collection to search, may be null
     * @param predicate
     *            the predicate to use, may be null
     * @return the first element of the collection which matches the predicate
     *         or null if none could be found
     */
    public static <E> E find(Collection<E> collection, Predicate<E> predicate) {
        if (collection != null && predicate != null) {
            for (Iterator<E> iter = collection.iterator(); iter.hasNext();) {
                E item = iter.next();
                if (predicate.evaluate(item)) {
                    return item;
                }
            }
        }
        return null;
    }

    /**
     * Filter the collection by applying a Predicate to each element. If the
     * predicate returns false, remove the element.
     * <p>
     * If the input collection or predicate is null, there is no change made.
     * 
     * @param collection
     *            the collection to get the input from, may be null
     * @param predicate
     *            the predicate to use as a filter, may be null
     */
    public static <E> void filter(Collection<E> collection, Predicate<E> predicate) {
        if (collection != null && predicate != null) {
            for (Iterator<E> it = collection.iterator(); it.hasNext();) {
                if (predicate.evaluate(it.next()) == false) {
                    it.remove();
                }
            }
        }
    }

    /**
     * Answers true if a predicate is true for at least one element of a
     * collection.
     * <p>
     * A <code>null</code> collection or predicate returns false.
     * 
     * @param collection
     *            the collection to get the input from, may be null
     * @param predicate
     *            the predicate to use, may be null
     * @return true if at least one element of the collection matches the
     *         predicate
     */
    public static <E> boolean exists(Collection<E> collection, Predicate<E> predicate) {
        if (collection != null && predicate != null) {
            for (Iterator<E> it = collection.iterator(); it.hasNext();) {
                if (predicate.evaluate(it.next())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Selects all elements from input collection which match the given
     * predicate
     * into an output collection.
     * <p>
     * A <code>null</code> predicate matches no elements.
     * 
     * @param inputCollection
     *            the collection to get the input from, may not be null
     * @param predicate
     *            the predicate to use, may be null
     * @return the elements matching the predicate (new list)
     * @throws NullPointerException
     *             if the input collection is null
     */
    public static <E> Collection<E> select(Collection<E> inputCollection, Predicate<E> predicate) {
        ArrayList<E> answer = new ArrayList<E>(inputCollection.size());
        select(inputCollection, predicate, answer);
        return answer;
    }

    /**
     * Selects all elements from input collection which match the given
     * predicate
     * and adds them to outputCollection.
     * <p>
     * If the input collection or predicate is null, there is no change to the
     * output collection.
     * 
     * @param inputCollection
     *            the collection to get the input from, may be null
     * @param predicate
     *            the predicate to use, may be null
     * @param outputCollection
     *            the collection to output into, may not be null
     */
    public static <E> void select(Collection<E> inputCollection, Predicate<E> predicate,
            Collection<E> outputCollection) {
        if (inputCollection != null && predicate != null) {
            for (Iterator<E> iter = inputCollection.iterator(); iter.hasNext();) {
                E item = iter.next();
                if (predicate.evaluate(item)) {
                    outputCollection.add(item);
                }
            }
        }
    }

    /**
     * Selects all elements from inputCollection which don't match the given
     * predicate
     * into an output collection.
     * <p>
     * If the input predicate is <code>null</code>, the result is an empty list.
     * 
     * @param inputCollection
     *            the collection to get the input from, may not be null
     * @param predicate
     *            the predicate to use, may be null
     * @return the elements <b>not</b> matching the predicate (new list)
     * @throws NullPointerException
     *             if the input collection is null
     */
    public static <E> Collection<E> selectRejected(Collection<E> inputCollection,
            Predicate<E> predicate) {
        ArrayList<E> answer = new ArrayList<E>(inputCollection.size());
        selectRejected(inputCollection, predicate, answer);
        return answer;
    }

    /**
     * Selects all elements from inputCollection which don't match the given
     * predicate
     * and adds them to outputCollection.
     * <p>
     * If the input predicate is <code>null</code>, no elements are added to
     * <code>outputCollection</code>.
     * 
     * @param inputCollection
     *            the collection to get the input from, may be null
     * @param predicate
     *            the predicate to use, may be null
     * @param outputCollection
     *            the collection to output into, may not be null
     */
    public static <E> void selectRejected(Collection<E> inputCollection, Predicate<E> predicate,
            Collection<E> outputCollection) {
        if (inputCollection != null && predicate != null) {
            for (Iterator<E> iter = inputCollection.iterator(); iter.hasNext();) {
                E item = iter.next();
                if (predicate.evaluate(item) == false) {
                    outputCollection.add(item);
                }
            }
        }
    }

    /**
     * Null-safe check if the specified collection is empty.
     * <p>
     * Null returns true.
     * 
     * @param coll
     *            the collection to check, may be null
     * @return true if empty or null
     * @since Commons Collections 3.2
     */
    public static <E> boolean isEmpty(Collection<E> coll) {
        return (coll == null || coll.isEmpty());
    }

    /**
     * Null-safe check if the specified collection is not empty.
     * <p>
     * Null returns false.
     * 
     * @param coll
     *            the collection to check, may be null
     * @return true if non-null and non-empty
     * @since Commons Collections 3.2
     */
    public static <E> boolean isNotEmpty(Collection<E> coll) {
        return !CollectionUtils.isEmpty(coll);
    }
}
