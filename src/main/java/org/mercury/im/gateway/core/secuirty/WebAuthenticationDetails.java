package org.mercury.im.gateway.core.secuirty;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.io.Serial;
import java.io.Serializable;

/**
 * A holder of selected HTTP details related to a web authentication request.
 */
public class WebAuthenticationDetails implements Serializable {

    @Serial
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private final String remoteAddress;

    private final String sessionId;

    /**
     * Records the remote address and will also set the session Id if a session already
     * exists (it won't create one).
     * @param request that the authentication request was received from
     */
    public WebAuthenticationDetails(HttpServletRequest request) {
        this(request.getRemoteAddr(), extractSessionId(request));
    }

    /**
     * Constructor to add Jackson2 serialize/deserialize support
     * @param remoteAddress remote address of current request
     * @param sessionId session id
     * @since 5.7
     */
    public WebAuthenticationDetails(String remoteAddress, String sessionId) {
        this.remoteAddress = remoteAddress;
        this.sessionId = sessionId;
    }

    private static String extractSessionId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (session != null) ? session.getId() : null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WebAuthenticationDetails) {
            WebAuthenticationDetails other = (WebAuthenticationDetails) obj;
            if ((this.remoteAddress == null) && (other.getRemoteAddress() != null)) {
                return false;
            }
            if ((this.remoteAddress != null) && (other.getRemoteAddress() == null)) {
                return false;
            }
            if (this.remoteAddress != null) {
                if (!this.remoteAddress.equals(other.getRemoteAddress())) {
                    return false;
                }
            }
            if ((this.sessionId == null) && (other.getSessionId() != null)) {
                return false;
            }
            if ((this.sessionId != null) && (other.getSessionId() == null)) {
                return false;
            }
            if (this.sessionId != null) {
                if (!this.sessionId.equals(other.getSessionId())) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Indicates the TCP/IP address the authentication request was received from.
     * @return the address
     */
    public String getRemoteAddress() {
        return this.remoteAddress;
    }

    /**
     * Indicates the <code>HttpSession</code> id the authentication request was received
     * from.
     * @return the session ID
     */
    public String getSessionId() {
        return this.sessionId;
    }

    @Override
    public int hashCode() {
        int code = 7654;
        if (this.remoteAddress != null) {
            code = code * (this.remoteAddress.hashCode() % 7);
        }
        if (this.sessionId != null) {
            code = code * (this.sessionId.hashCode() % 7);
        }
        return code;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(" [");
        sb.append("RemoteIpAddress=").append(this.getRemoteAddress()).append(", ");
        sb.append("SessionId=").append(this.getSessionId()).append("]");
        return sb.toString();
    }

}
