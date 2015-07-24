package io.github.vdubois.tracker.domain;

import lombok.Data;

/**
 * Created by vdubois on 24/07/15.
 */
@Data
public class Point {

    /** abscisse (date) */
    private String date;

    /** ordonnée (prix) */
    private String value;
}
