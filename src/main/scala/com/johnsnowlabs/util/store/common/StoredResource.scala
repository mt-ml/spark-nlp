package com.johnsnowlabs.util.store.common

case class StoredResource[A](path: String, storePath: String, resource: A) {

}