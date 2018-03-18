package io.github.spair.byond.message;

import java.util.Objects;

/**
 * Container for converted BYOND response.<br>
 * Response represented as {@link java.lang.Object} class, so type cast is necessary.
 */
@SuppressWarnings("unused")
public class ByondResponse {

    private Object response;
    private ResponseType responseType;

    public ByondResponse() {
    }

    public ByondResponse(final Object response, final ResponseType responseType) {
        this.response = response;
        this.responseType = responseType;
    }

    public Object getResponse() {
        return response;
    }

    public String getResponseAsString() {
        return (String) response;
    }

    public Float getResponseAsFloat() {
        return (Float) response;
    }

    public void setResponse(final Object response) {
        this.response = response;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(final ResponseType responseType) {
        this.responseType = responseType;
    }

    @Override
    public String toString() {
        return "ByondResponse{"
                + "response=" + response
                + ", responseType=" + responseType
                + '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ByondResponse that = (ByondResponse) o;
        return Objects.equals(response, that.response)
                && responseType == that.responseType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(response, responseType);
    }
}
