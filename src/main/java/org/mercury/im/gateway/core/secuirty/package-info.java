/**
 * 注意 ServletRequestMethodArgumentResolver 类的实现 其中会拦截Principal及子类的参数,所以AuthenticationArgumentResolver无法被使用到
 * 所以无法自定义过滤器的情况下放置参数Authentication
 */
package org.mercury.im.gateway.core.secuirty;