// Copyright 2019 Axel Howind
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.dua3.fx.application;

import java.lang.reflect.InvocationTargetException;
import java.util.prefs.Preferences;

public abstract class FxSettings<S extends FxSettings> {

	public static <T extends FxSettings> T copyOf(T other) {
		try {
			T inst = (T) other.getClass().getConstructor().newInstance();
			inst.assign(other);
			return inst;
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new IllegalStateException("could not create Settings instance", e);
		}
	}

	public static <T extends FxSettings> T fromPreferences(Class<T> cls, Preferences node) {
		try {
			T inst = (T) cls.getConstructor().newInstance();
			inst.load(node);
			return inst;
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new IllegalStateException("could not create Settings instance", e);
		}
	}
	
	public abstract void load(Preferences node);
	public abstract void store(Preferences node);
	public abstract void assign(S other);

}
