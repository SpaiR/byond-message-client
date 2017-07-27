package io.github.spair.byond.message.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ByondResponse {

    private Object responseData;
    private ResponseType responseType;
}
