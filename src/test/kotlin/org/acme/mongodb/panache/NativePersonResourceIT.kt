package org.acme.mongodb.panache

import io.quarkus.test.junit.NativeImageTest

@NativeImageTest
class NativePersonResourceIT : PersonResourceTest() { // Execute the same tests but in native mode.
}
