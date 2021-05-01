package org.example.hackernews;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Domain object of HackerNews item.
 *
 * This class declares all the possible properties, the concrete schema is decided by {@link GenericItem#type} property.
 * Some wrapper class like {@link CommentItem} can be used to expose concrete schema of specific type of item.
 *
 * @see <a href="https://github.com/HackerNews/API">HackerNews/API</a> for schema detail.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GenericItem {

    private long id;
    private boolean deleted;
    private ItemType type;
    private String by;
    private long time;
    private String text;
    private boolean dead;
    private long parent;
    private long poll;
    private long[] kids;
    private String url;
    private long score;
    private String title;
    private int[] parts;
    private long descendants;

}
