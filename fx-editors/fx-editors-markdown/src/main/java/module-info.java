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

module com.dua3.fx.editors.markdown {
	exports com.dua3.fx.editors.markdown;
	opens com.dua3.fx.editors.markdown;

	requires com.dua3.fx.editors;
	requires com.dua3.fx.util;
	requires com.dua3.fx.web;

	requires com.dua3.utility;
	requires com.dua3.utility.json;

	requires java.logging;
	requires java.prefs;
	requires jdk.jsobject;
	requires javafx.fxml;
	requires javafx.controls;
	requires org.json;
}
