package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.data.repositories.wrappers

enum class ResponseStatus {
    /**
     * Cannot connect to web api, but cached data is returned.
     */
    OFFLINE,

    /**
     * Response from web api is returned.
     */
    OK,

    /**
     * Web api responded with unauthorized. No data returned.
     */
    UNAUTHORIZED,

    /**
     * Web api responded with 404 or we do not have cached data. No data returned.
     */
    NOTFOUND,

    /**
     * DB related error. No data returned.
     */
    DBERROR,

    /**
     * Something went wrong. No data returned.
     */
    UNDEFINEDERROR
}