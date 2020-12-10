# Xplosion

Xplosion is a fork of [SmallBang](https://github.com/hanks-zyh/SmallBang) that has been converted to Kotlin and updated for the latest version of Android :smile:

Twitter like animation for any view :heartbeat:

## Usage

```groovy
dependencies {
    implementation '[test]'
}
```

```xml
<org.bandev.libraries.bang.SmallBangView
    android:id="@+id/like_heart"
    android:layout_width="56dp"
    android:layout_height="56dp">

    <ImageView
        android:id="@+id/image"
        android:layout_width="20dp"
        android:layout_height="20dp"
        android:layout_gravity="center"
        android:src="@drawable/heart_selector"
        android:text="Hello World!"/>
</org.bandev.libraries.bang.SmallBangView>
```
or

```xml

<org.bandev.libraries.bang.SmallBangView
    android:id="@+id/like_text"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:circle_end_color="#ffbc00"
    app:circle_start_color="#fa9651"
    app:dots_primary_color="#fa9651"
    app:dots_secondary_color="#ffbc00">

    <TextView
        android:id="@+id/text"
        android:layout_width="50dp"
        android:layout_height="20dp"
        android:layout_gravity="center"
        android:gravity="center"
        android:text="hanks"
        android:textColor="@color/text_selector"
        android:textSize="14sp"/>
</org.bandev.libraries.bang.SmallBangView>
```

## License

This library is licensed under the [Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

See [`LICENSE`](LICENSE) for full of the license text.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
