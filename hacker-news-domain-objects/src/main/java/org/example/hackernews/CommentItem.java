package org.example.hackernews;

import java.security.InvalidParameterException;
import java.util.Optional;

/**
 * A wrapper class for Comment type of HackerNews item
 */
public class CommentItem {

    private final GenericItem item;

    /**
     * Wrapping HackerNews {@link GenericItem} as a item with comment schema
     * @param item the generic HackerNews item
     * @return A Hackernews item with comment schema
     */
    public static CommentItem fromGenericItem(GenericItem item) {
        return new CommentItem(item);
    }

    /**
     * Wrapping HackerNews {@link GenericItem} as a item with comment schema
     * @param item the generic HackerNews item
     * @return A Hackernews item with comment schema
     */
    public static Optional<CommentItem> fromGenericItemAsOptional(GenericItem item) {
        if(item.getType() == ItemType.comment){
            try {
                return Optional.of(fromGenericItem(item));
            } catch (BadItemTypeException ignored) {
                /* This should never happened */
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    private CommentItem(GenericItem item) {
        if(item == null || item.getType() != ItemType.comment)
            throw new BadItemTypeException();
        this.item = item;
    }

    public String getAuthor() { return item.getBy(); }
    public long getId() { return item.getId(); }
    public long[] getCommentIds() { return item.getKids(); }
    public long getParent() { return item.getParent(); }
    public String getText() { return item.getText(); }
    public long getTime() { return item.getTime(); }
}
