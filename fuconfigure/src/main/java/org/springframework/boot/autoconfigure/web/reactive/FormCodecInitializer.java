/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.autoconfigure.web.reactive;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.FormHttpMessageReader;
import org.springframework.http.codec.FormHttpMessageWriter;

/**
 * {@link ApplicationContextInitializer} adapter for registering Form codecs.
 */
public class FormCodecInitializer extends AbstractCodecInitializer {

    public FormCodecInitializer(boolean isClientCodec) {
        super(isClientCodec);
    }

    @Override
    protected void register(GenericApplicationContext context, CodecConfigurer configurer) {
        configurer.customCodecs().writer(new FormHttpMessageWriter());
        configurer.customCodecs().reader(new FormHttpMessageReader());
    }
}
