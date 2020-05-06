/**
 * Copyright 2020 E.Luinstra
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bitbucket.eluinstra.fs.service.web;

import java.util.Date;

import org.apache.wicket.model.IModel;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DateTextField extends org.apache.wicket.extensions.markup.html.form.DateTextField
{
	private static final long serialVersionUID = 1L;
	Supplier<Boolean> isRequired;

	@Builder
	public DateTextField(String id, IModel<Date> model, String datePattern, Supplier<Boolean> isRequired)
	{
		super(id,model,datePattern);
		this.isRequired = isRequired;
	}

	@Override
	public boolean isRequired()
	{
		return isRequired.get();
	}
}
