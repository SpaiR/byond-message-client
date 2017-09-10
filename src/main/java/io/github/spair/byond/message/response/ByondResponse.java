package io.github.spair.byond.message.response;

/**
 * Container for converted BYOND server response.
 * <br>
 * Contains response data as {@link java.lang.Object}, so type cast is necessary.
 * Also has actual response type as {@link io.github.spair.byond.message.response.ResponseType}.
 */
@SuppressWarnings("unused")
public class ByondResponse {

    private Object responseData;
    private ResponseType responseType;

    public ByondResponse() {}

    public ByondResponse(Object responseData, ResponseType responseType) {
        this.responseData = responseData;
        this.responseType = responseType;
    }

    public Object getResponseData() {
        return responseData;
    }

    public void setResponseData(Object responseData) {
        this.responseData = responseData;
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

        return (responseData != null ? responseData.equals(that.responseData) : that.responseData == null)
                && responseType == that.responseType;
    }

    @Override
    public int hashCode() {
        int result = responseData != null ? responseData.hashCode() : 0;
        result = 31 * result + (responseType != null ? responseType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ByondResponse{" +
                "responseData=" + responseData +
                ", responseType=" + responseType +
                '}';
    }
}
