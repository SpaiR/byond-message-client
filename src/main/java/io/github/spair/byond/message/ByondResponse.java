package io.github.spair.byond.message;

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

    public ByondResponse(Object response, ResponseType responseType) {
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

    public void setResponse(Object response) {
        this.response = response;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(ResponseType responseType) {
        this.responseType = responseType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ByondResponse that = (ByondResponse) o;

        return (response != null ? response.equals(that.response) : that.response == null)
                && responseType == that.responseType;
    }

    @Override
    public int hashCode() {
        int result = response != null ? response.hashCode() : 0;
        result = 31 * result + (responseType != null ? responseType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ByondResponse{" +
                "response=" + response +
                ", responseType=" + responseType +
                '}';
    }
}
