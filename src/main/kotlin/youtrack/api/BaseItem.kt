package youtrack.api

import com.fasterxml.jackson.annotation.JacksonInject

/**
 * Created by bram on 12/8/16.
 */
open class BaseItem(@JacksonInject val context: MethodContext) {
}