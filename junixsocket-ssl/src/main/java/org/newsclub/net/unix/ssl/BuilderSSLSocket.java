/*
 * junixsocket
 *
 * Copyright 2009-2023 Christian Kohlschütter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.newsclub.net.unix.ssl;

import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.function.BiFunction;

import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

/**
 * A wrapper for {@link SSLSocket} instances, with certain modifications to navigate in the real
 * world.
 * <p>
 * Modified behavior:
 * <ol>
 * <li>Ignore {@link SocketException}s thrown upon {@link #close()}, including
 * {@link #getInputStream()} and {@link #getOutputStream()}; however attempt closing the underlying
 * socket upon {@link #close()} if desired. On Android, "broken pipe" exceptions may be thrown upon
 * close.</li>
 * </ol>
 *
 *
 * @author Christian Kohlschütter
 */
@SuppressWarnings("PMD.ExcessivePublicCount")
final class BuilderSSLSocket extends BuilderSSLSocketShim {
  BuilderSSLSocket(SSLSocket socket, Socket underlyingSocket, boolean doCloseUnderlyingSocket) {
    super(socket, underlyingSocket, doCloseUnderlyingSocket);
  }

  @Override
  public String[] getSupportedCipherSuites() {
    return sslSocket.getSupportedCipherSuites();
  }

  @Override
  public String[] getEnabledCipherSuites() {
    return sslSocket.getEnabledCipherSuites();
  }

  @Override
  public void setEnabledCipherSuites(String[] suites) {
    sslSocket.setEnabledCipherSuites(suites);
  }

  @Override
  public String[] getSupportedProtocols() {
    return sslSocket.getSupportedProtocols();
  }

  @Override
  public String[] getEnabledProtocols() {
    return sslSocket.getEnabledProtocols();
  }

  @Override
  public void setEnabledProtocols(String[] protocols) {
    sslSocket.setEnabledProtocols(protocols);
  }

  @Override
  public SSLSession getSession() {
    return sslSocket.getSession();
  }

  @Override
  public void addHandshakeCompletedListener(HandshakeCompletedListener listener) {
    sslSocket.addHandshakeCompletedListener(listener);
  }

  @Override
  public void removeHandshakeCompletedListener(HandshakeCompletedListener listener) {
    sslSocket.removeHandshakeCompletedListener(listener);
  }

  @Override
  public void startHandshake() throws IOException {
    sslSocket.startHandshake();
  }

  @Override
  public void setUseClientMode(boolean mode) {
    sslSocket.setUseClientMode(mode);
  }

  @Override
  public boolean getUseClientMode() {
    return sslSocket.getUseClientMode();
  }

  @Override
  public void setNeedClientAuth(boolean need) {
    sslSocket.setNeedClientAuth(need);
  }

  @Override
  public boolean getNeedClientAuth() {
    return sslSocket.getNeedClientAuth();
  }

  @Override
  public void setWantClientAuth(boolean want) {
    sslSocket.setWantClientAuth(want);
  }

  @Override
  public boolean getWantClientAuth() {
    return sslSocket.getWantClientAuth();
  }

  @Override
  public void setEnableSessionCreation(boolean flag) {
    sslSocket.setEnableSessionCreation(flag);
  }

  @Override
  public boolean getEnableSessionCreation() {
    return sslSocket.getEnableSessionCreation();
  }

  @Override
  public SSLSession getHandshakeSession() {
    return sslSocket.getHandshakeSession();
  }

  @Override
  public SSLParameters getSSLParameters() {
    return sslSocket.getSSLParameters();
  }

  @Override
  public void setSSLParameters(SSLParameters params) {
    sslSocket.setSSLParameters(params);
  }

  @Override
  public String getApplicationProtocol() {
    return sslSocket.getApplicationProtocol();
  }

  @Override
  public String getHandshakeApplicationProtocol() {
    return sslSocket.getHandshakeApplicationProtocol();
  }

  @Override
  public void setHandshakeApplicationProtocolSelector(
      BiFunction<SSLSocket, List<String>, String> selector) {
    sslSocket.setHandshakeApplicationProtocolSelector(selector);
  }

  @Override
  public BiFunction<SSLSocket, List<String>, String> getHandshakeApplicationProtocolSelector() {
    return sslSocket.getHandshakeApplicationProtocolSelector();
  }

  @Override
  public void connect(SocketAddress endpoint) throws IOException {
    sslSocket.connect(endpoint);
  }

  @Override
  public void connect(SocketAddress endpoint, int timeout) throws IOException {
    sslSocket.connect(endpoint, timeout);
  }

  @Override
  public void bind(SocketAddress bindpoint) throws IOException {
    sslSocket.bind(bindpoint);
  }

  @Override
  public InetAddress getInetAddress() {
    return sslSocket.getInetAddress();
  }

  @Override
  public InetAddress getLocalAddress() {
    return sslSocket.getLocalAddress();
  }

  @Override
  public int getPort() {
    return sslSocket.getPort();
  }

  @Override
  public int getLocalPort() {
    return sslSocket.getLocalPort();
  }

  @Override
  public SocketAddress getRemoteSocketAddress() {
    return sslSocket.getRemoteSocketAddress();
  }

  @Override
  public SocketAddress getLocalSocketAddress() {
    return sslSocket.getLocalSocketAddress();
  }

  @Override
  public SocketChannel getChannel() {
    return sslSocket.getChannel();
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return new FilterInputStream(sslSocket.getInputStream()) {

      @Override
      public void close() throws IOException {
        try {
          super.close();
        } catch (SocketException e) {
          // BouncyCastle may throw "broken pipe" upon close
          // ignore
        }
      }
    };
  }

  @Override
  public OutputStream getOutputStream() throws IOException {
    return new FilterOutputStream(sslSocket.getOutputStream()) {

      @Override
      public void close() throws IOException {
        try {
          super.close();
        } catch (SocketException e) {
          // BouncyCastle may throw "broken pipe" upon close
          // ignore
        }
      }
    };
  }

  @Override
  public void setTcpNoDelay(boolean on) throws SocketException {
    sslSocket.setTcpNoDelay(on);
  }

  @Override
  public boolean getTcpNoDelay() throws SocketException {
    return sslSocket.getTcpNoDelay();
  }

  @Override
  public void setSoLinger(boolean on, int linger) throws SocketException {
    sslSocket.setSoLinger(on, linger);
  }

  @Override
  public int getSoLinger() throws SocketException {
    return sslSocket.getSoLinger();
  }

  @Override
  public void sendUrgentData(int data) throws IOException {
    sslSocket.sendUrgentData(data);
  }

  @Override
  public void setOOBInline(boolean on) throws SocketException {
    sslSocket.setOOBInline(on);
  }

  @Override
  public boolean getOOBInline() throws SocketException {
    return sslSocket.getOOBInline();
  }

  @Override
  public synchronized void setSoTimeout(int timeout) throws SocketException {
    sslSocket.setSoTimeout(timeout);
  }

  @Override
  public synchronized int getSoTimeout() throws SocketException {
    return sslSocket.getSoTimeout();
  }

  @Override
  public synchronized void setSendBufferSize(int size) throws SocketException {
    sslSocket.setSendBufferSize(size);
  }

  @Override
  public synchronized int getSendBufferSize() throws SocketException {
    return sslSocket.getSendBufferSize();
  }

  @Override
  public synchronized void setReceiveBufferSize(int size) throws SocketException {
    sslSocket.setReceiveBufferSize(size);
  }

  @Override
  public synchronized int getReceiveBufferSize() throws SocketException {
    return sslSocket.getReceiveBufferSize();
  }

  @Override
  public void setKeepAlive(boolean on) throws SocketException {
    sslSocket.setKeepAlive(on);
  }

  @Override
  public boolean getKeepAlive() throws SocketException {
    return sslSocket.getKeepAlive();
  }

  @Override
  public void setTrafficClass(int tc) throws SocketException {
    sslSocket.setTrafficClass(tc);
  }

  @Override
  public int getTrafficClass() throws SocketException {
    return sslSocket.getTrafficClass();
  }

  @Override
  public void setReuseAddress(boolean on) throws SocketException {
    sslSocket.setReuseAddress(on);
  }

  @Override
  public boolean getReuseAddress() throws SocketException {
    return sslSocket.getReuseAddress();
  }

  @Override
  public synchronized void close() throws IOException {
    try {
      sslSocket.close();
    } catch (SocketException e) {
      // BouncyCastle may throw "broken pipe" upon close
      // ignore, but make sure we close the underlying socket if desired
      if (doCloseUnderlyingSocket) {
        Socket s = underlyingSocket;
        if (s != null) {
          try {
            s.close();
          } catch (IOException e1) {
            e1.addSuppressed(e);
            throw e1;
          }
        }
      }
    }
  }

  @Override
  public void shutdownInput() throws IOException {
    sslSocket.shutdownInput();
  }

  @Override
  public void shutdownOutput() throws IOException {
    sslSocket.shutdownOutput();
  }

  @Override
  public String toString() {
    return sslSocket.toString();
  }

  @Override
  public boolean isConnected() {
    return sslSocket.isConnected();
  }

  @Override
  public boolean isBound() {
    return sslSocket.isBound();
  }

  @Override
  public boolean isClosed() {
    return sslSocket.isClosed();
  }

  @Override
  public boolean isInputShutdown() {
    return sslSocket.isInputShutdown();
  }

  @Override
  public boolean isOutputShutdown() {
    return sslSocket.isOutputShutdown();
  }

  @Override
  public void setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
    sslSocket.setPerformancePreferences(connectionTime, latency, bandwidth);
  }
}
