/*
 * Copyright (c) 2008 DawningStreams, Inc.  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without 
 *  modification, are permitted provided that the following conditions are met:
 *  
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *  
 *  2. Redistributions in binary form must reproduce the above copyright notice, 
 *     this list of conditions and the following disclaimer in the documentation 
 *     and/or other materials provided with the distribution.
 *  
 *  3. The end-user documentation included with the redistribution, if any, must 
 *     include the following acknowledgment: "This product includes software 
 *     developed by DawningStreams, Inc." 
 *     Alternately, this acknowledgment may appear in the software itself, if 
 *     and wherever such third-party acknowledgments normally appear.
 *  
 *  4. The name "DawningStreams,Inc." must not be used to endorse or promote
 *     products derived from this software without prior written permission.
 *     For written permission, please contact DawningStreams,Inc. at 
 *     http://www.dawningstreams.com.
 *  
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 *  INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 *  DAWNINGSTREAMS, INC OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 *  OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 *  EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 *  DawningStreams is a registered trademark of DawningStreams, Inc. in the United 
 *  States and other countries.
 *  
 */

package Examples.G_Simple_Pipe_Communication;

import Examples.Z_Tools_And_Others.Tools;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import net.jxta.credential.AuthenticationCredential;
import net.jxta.credential.Credential;
import net.jxta.document.AdvertisementFactory;
import net.jxta.endpoint.Message;
import net.jxta.exception.PeerGroupException;
import net.jxta.exception.ProtocolNotSupportedException;
import net.jxta.id.IDFactory;
import net.jxta.impl.membership.pse.FileKeyStoreManager;
import net.jxta.impl.membership.pse.PSEConfig;
import net.jxta.impl.membership.pse.PSEMembershipService;
import net.jxta.impl.membership.pse.PSEUtils;
import net.jxta.impl.membership.pse.StringAuthenticator;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.pipe.PipeService;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.PipeAdvertisement;

public class RendezVous_Chandra_Receiving_Messages implements PipeMsgListener {
    
    // PipeService.UnicastType or PipeService.UnicastSecureType or PipeService.PropagateType
    public static final String PipeType = PipeService.UnicastType;

    // Static attributes
    public static final String Name = "RendezVous Chandra, Receiving Messages";
    public static final int TcpPort = 9722;
    public static final PeerID PID = IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID, Name.getBytes());
    public static final File ConfigurationFile = new File("." + System.getProperty("file.separator") + Name);
    
    public static final String MyPrincipalName = "Principal - " + Name;
    public static final String MyPrivateKeyPassword = "Private Key Password - " + Name;

    public static final String MyKeyStoreFileName = "MyKeyStoreFile";
    public static final String MyKeyStoreLocation = "." + System.getProperty("file.separator") + Name + File.separator + "MyKeyStoreLocation";
    public static final String MyKeyStorePassword = "KeyStore Password - " + Name;
    public static final String MyKeyStoreProvider = "KeyStore Provider - " + Name;

    public static final File MyKeyStoreDirectory = new File(MyKeyStoreLocation);
    public static final File MyKeyStoreFile = new File(MyKeyStoreLocation + File.separator + MyKeyStoreFileName);
    
    public static final String Base64X509Certificate = 
        "MIICSDCCAbGgAwIBAgIBATANBgkqhkiG9w0BAQUFADBqMRUwEwYDVQQKDAx3d3cuanh0YS5vcmcx" +
        "MjAwBgNVBAMMKVJlbmRlelZvdXMgQ2hhbmRyYSwgUmVjZWl2aW5nIE1lc3NhZ2VzLUNBMR0wGwYD" +
        "VQQLDBRBMTk2MEJFNEFCMUIyM0FGOEM4MDAeFw0wODA2MDIyMzI4NDFaFw0xODA2MDIyMzI4NDFa" +
        "MGoxFTATBgNVBAoMDHd3dy5qeHRhLm9yZzEyMDAGA1UEAwwpUmVuZGV6Vm91cyBDaGFuZHJhLCBS" +
        "ZWNlaXZpbmcgTWVzc2FnZXMtQ0ExHTAbBgNVBAsMFEExOTYwQkU0QUIxQjIzQUY4QzgwMIGfMA0G" +
        "CSqGSIb3DQEBAQUAA4GNADCBiQKBgQCW15ZwSzj/a9nJhbuhnsK9C2y7GT250k6FocNDUenMivJ7" +
        "E08phz62Yr06+OXCBhMvYgj04TGjnyVcXZ6A0if3MyejdvrIzQD/8/bpUN52LINjRwIomw/AMMxT" +
        "RblvaP4kHs0k57ejgfdzCaQj7BsKZeb1wRs16ywal4E1T7OdWwIDAQABMA0GCSqGSIb3DQEBBQUA" +
        "A4GBADSfENLxrw3J4/dbHkTzY+NC9niAjCZcAEYKYPhG6gQDZz0T8r/2SFAUwyYdi6BGDMQq5cNW" +
        "BzFeAwQSS8wSmAb4bS9nZtDygpUATs/7D/WNNmSAYa3Sq+qYNMJtEgCfyTYDKUKowhyuV6j420Wk" +
        "9ojK6r7B+hZKGUWK4A5Xw2ZZ";
        
    public static final String Base64PrivateKey = 
        "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJbXlnBLOP9r2cmFu6Gewr0LbLsZ" +
        "PbnSToWhw0NR6cyK8nsTTymHPrZivTr45cIGEy9iCPThMaOfJVxdnoDSJ/czJ6N2+sjNAP/z9ulQ" +
        "3nYsg2NHAiibD8AwzFNFuW9o/iQezSTnt6OB93MJpCPsGwpl5vXBGzXrLBqXgTVPs51bAgMBAAEC" +
        "gYBqVUmomW5xqB6b0BlHwnn29/HLJuz7bESMMU9Lypes6BotJpx8YumTeqF4y/JzRwMJOOulbIYo" +
        "mMymgxx0JTGkG29ZxhQYROjsmdkTSaYK4mV5MmPju8AT1lmgNc0j9mh/QhDz9MgfNiR+hkMuqCK5" +
        "S9pZdXIHkJrvD8AfZVKUcQJBANvbJ5mkqICa8RfGxvmZbLVqCck3vJTEm2GS5myPkYujiwJ+5/Ek" +
        "IpBr+UVCkbzUSmNhPUe+j26afjcm2S2FrokCQQCvo+sCLk91vxHdXr8QwtNp2nyEsdLd1oORZH0X" +
        "xapma2ro+z8s9SEfczwLZjy5z5mLfL33cdh1poq+8lEQr5PDAkBYKGWVpz/l6bs5BHzuwwWjLPTl" +
        "mZLluHxxwLzi1SLQcnCgPJD20b2GWbVnf0z/AcUeWnR/GztaZ1qq9MnrBgq5AkBMUPEAFZc5Fjhw" +
        "84YZhk3OJL0N+yYgdeDc/8jK13xe3DWr3d9pbWli/PMOEPI52lZhIWZ5aeIf3KlUpn1Kvr95AkEA" +
        "sQ06yTMFKjq34SZFAoe/Yw6qN6uCQRxXhCCzuGaaj8mDzV07vQ9JFi263IgkXkKa8fEfmf/4UJno" +
        "gqzOn9C4cg==";

    // Catching messages
    public void pipeMsgEvent(PipeMsgEvent PME) {
        
        // We received a message
        Message ReceivedMessage = PME.getMessage();
        String TheText = ReceivedMessage.getMessageElement("DummyNameSpace", "HelloElement").toString();

        // Notifying the user
        Tools.PopInformationMessage(Name, "Received message:\n\n" + TheText);
        
    }
    
    public static PipeAdvertisement GetPipeAdvertisement() {
        
        // Creating a Pipe Advertisement
        PipeAdvertisement MyPipeAdvertisement = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(PipeAdvertisement.getAdvertisementType());
        PipeID MyPipeID = IDFactory.newPipeID(PeerGroupID.defaultNetPeerGroupID, Name.getBytes());

        MyPipeAdvertisement.setPipeID(MyPipeID);
        MyPipeAdvertisement.setType(PipeType);
        MyPipeAdvertisement.setName("Test Pipe");
        MyPipeAdvertisement.setDescription("Created by " + Name);
        
        return MyPipeAdvertisement;
        
    }
    
    public static void main(String[] args) {
        
        try {
            
            // Removing any existing configuration?
            Tools.CheckForExistingConfigurationDeletion(Name, ConfigurationFile);
            
            // Recreating the X509 certificate
            X509Certificate TheX509Certificate = null;
            X509Certificate OtherPeerX509Certificate = null;
            PrivateKey ThePrivateKey = null;
    
            byte[] Temp = PSEUtils.base64Decode(new StringReader(Base64X509Certificate));

            CertificateFactory TheCF = CertificateFactory.getInstance("X509");
            TheX509Certificate = (X509Certificate) TheCF.generateCertificate(new ByteArrayInputStream(Temp));

            Temp = PSEUtils.base64Decode(new StringReader(Edge_Dimitri_Sending_Messages.Base64X509Certificate));
            OtherPeerX509Certificate = (X509Certificate) TheCF.generateCertificate(new ByteArrayInputStream(Temp));

            // Restoring the private key
            Temp = PSEUtils.base64Decode(new StringReader(Base64PrivateKey));

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec MyPKCS8EncodedKeySpec = new PKCS8EncodedKeySpec(Temp);
            ThePrivateKey = keyFactory.generatePrivate(MyPKCS8EncodedKeySpec);

            // Preparing data
            MyKeyStoreDirectory.mkdirs();
            
            // Creating the key store
            FileKeyStoreManager MyFileKeyStoreManager = new FileKeyStoreManager(
                (String)null, MyKeyStoreProvider, MyKeyStoreFile);
            
            MyFileKeyStoreManager.createKeyStore(MyKeyStorePassword.toCharArray());
            
            // Loading the (empty) keystore 
            KeyStore MyKeyStore = MyFileKeyStoreManager.loadKeyStore(MyKeyStorePassword.toCharArray());
            
            // Setting private key and certificate
            X509Certificate[] TempX509 = { TheX509Certificate };
            MyKeyStore.setKeyEntry(PID.toString(), ThePrivateKey, MyPrivateKeyPassword.toCharArray(), TempX509);
            
            MyKeyStore.setCertificateEntry(Edge_Dimitri_Sending_Messages.PID.toString(), OtherPeerX509Certificate);
            
            // Saving the data
            MyFileKeyStoreManager.saveKeyStore(MyKeyStore, MyKeyStorePassword.toCharArray());

            // Creation of network manager
            NetworkManager MyNetworkManager = new NetworkManager(NetworkManager.ConfigMode.RENDEZVOUS,
                    Name, ConfigurationFile.toURI());
            
            // Retrieving the network configurator
            NetworkConfigurator MyNetworkConfigurator = MyNetworkManager.getConfigurator();
            
            // Setting the key store location
            MyNetworkConfigurator.setKeyStoreLocation(MyKeyStoreFile.toURI());
            
            // Setting more configuration
            MyNetworkConfigurator.setTcpPort(TcpPort);
            MyNetworkConfigurator.setTcpEnabled(true);
            MyNetworkConfigurator.setTcpIncoming(true);
            MyNetworkConfigurator.setTcpOutgoing(true);
            Tools.CheckForMulticastUsage(Name, MyNetworkConfigurator);

            // Setting the Peer ID
            Tools.PopInformationMessage(Name, "Setting the peer ID to :\n\n" + PID.toString());
            MyNetworkConfigurator.setPeerID(PID);
            
            // Saving the configuration
            MyNetworkConfigurator.save();

            // Starting the JXTA network
            Tools.PopInformationMessage(Name, "Start the JXTA network");
            PeerGroup NetPeerGroup = MyNetworkManager.startNetwork();
            
            // Retrieving the PSE membership service
            PSEMembershipService MyMembershipService = (PSEMembershipService) NetPeerGroup.getMembershipService();
            
            // Joining the peer group
            AuthenticationCredential MyAuthenticationCredit = new
                AuthenticationCredential( NetPeerGroup, "StringAuthentication", null );

            StringAuthenticator MyStringAuthenticator = (StringAuthenticator) MyMembershipService.apply(MyAuthenticationCredit);

            MyStringAuthenticator.setAuth1_KeyStorePassword(MyKeyStorePassword);
            MyStringAuthenticator.setAuth2Identity(PID);
            MyStringAuthenticator.setAuth3_IdentityPassword(MyPrivateKeyPassword);

            Credential MyCredential = null;

            if (MyStringAuthenticator.isReadyForJoin()) {
                MyCredential = MyMembershipService.join(MyStringAuthenticator);
            }
            
            if (MyCredential!=null) {
                Tools.PopInformationMessage(Name, "Credentials created successfully");
            } else {
                Tools.PopInformationMessage(Name, "Credentials NOT created successfully");
            }
            
            // Check key store content
            PSEConfig MyPSEConfig = MyMembershipService.getPSEConfig();
            
            X509Certificate MyX509Check = MyPSEConfig.getTrustedCertificate(PID);
            
            if (MyX509Check==null) {
                Tools.PopInformationMessage(Name, "X509 not found!");
            } else {
                Tools.PopInformationMessage(Name, "X509 found!");
            }
            
            X509Certificate OtherPeerX509Check = MyPSEConfig.getTrustedCertificate(Edge_Dimitri_Sending_Messages.PID);
            
            if (OtherPeerX509Check==null) {
                Tools.PopInformationMessage(Name, "X509 for other peer not found!");
            } else {
                Tools.PopInformationMessage(Name, "X509 for other peer found!");
            }

            PrivateKey MyPrivateKeyCheck = MyPSEConfig.getKey(PID, MyPrivateKeyPassword.toCharArray());
            
            if (MyPrivateKeyCheck==null) {
                Tools.PopInformationMessage(Name, "Private key not found!");
            } else {
                Tools.PopInformationMessage(Name, "Private key found!");
            }

            // Waiting for other peers to connect to JXTA
            Tools.PopInformationMessage(Name, "Waiting for other peers to connect to JXTA");

            // Creating an input pipe
            PipeService MyPipeService = NetPeerGroup.getPipeService();
            MyPipeService.createInputPipe(GetPipeAdvertisement(), new RendezVous_Chandra_Receiving_Messages());
            
            // Displaying pipe advertisement to identify potential compilation issues
            System.out.println(RendezVous_Chandra_Receiving_Messages.GetPipeAdvertisement().toString());
            
            // Going to sleep for some time
            Tools.GoToSleep(60000);
            
            // Retrieving connected peers
            Tools.popConnectedPeers(NetPeerGroup.getRendezVousService(), Name);
            
            // Resigning all credentials
            MyMembershipService.resign();

            // Stopping the network
            Tools.PopInformationMessage(Name, "Stop the JXTA network");
            MyNetworkManager.stopNetwork();
            
        } catch (ProtocolNotSupportedException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        } catch (InvalidKeySpecException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        } catch (NoSuchAlgorithmException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        } catch (CertificateException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        } catch (NoSuchProviderException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());

        } catch (KeyStoreException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        } catch (IOException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        } catch (PeerGroupException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        }

    }

}