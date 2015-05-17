/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.sf.redi;

import java.util.List;

public interface RelationshipFinder<
        P extends PartOfSpeech,
        I extends UsageId,
        T extends PointerType> {
    List<Relationship<I, T>> findRelationships(
            Usage<P, T, I> source, int depth, int maxResults);
    List<Relationship<I, T>> findRelationships(
            Usage<P, T, I> source, PointerType<T, P> type, int depth, int maxResults);
    List<Relationship<I, T>> findRelationships(
            Usage<P, T, I> source, Usage<P, T, I> target, int depth, int maxResults);
    List<Relationship<I, T>> findRelationships(
            Usage<P, T, I> source, Usage<P, T, I> target, PointerType<T, P> type, int depth, int maxResults);    
}