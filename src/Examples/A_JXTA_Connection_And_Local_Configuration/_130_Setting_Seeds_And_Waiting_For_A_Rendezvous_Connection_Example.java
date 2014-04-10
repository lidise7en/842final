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

package Examples.A_JXTA_Connection_And_Local_Configuration;

import Examples.Z_Tools_And_Others.Tools;
import java.io.IOException;
import java.util.Enumeration;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.ID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkManager;
import net.jxta.rendezvous.RendezVousService;

public class _130_Setting_Seeds_And_Waiting_For_A_Rendezvous_Connection_Example {
    
     public static final String Name = "Example 130";
     
     public static void main(String[] args) {
        
        try {

            // Creation of the network manager
            NetworkManager MyNetworkManager = new NetworkManager(NetworkManager.ConfigMode.EDGE,
                    Name);

            // Setting the public Rendezvous and Relays
            Tools.PopInformationMessage(Name, "Setting the rendezvous and relay seeds");
            MyNetworkManager.setUseDefaultSeeds(true);

            // Starting the JXTA network
            Tools.PopInformationMessage(Name, "Starting the JXTA network");
            PeerGroup NetPeerGroup = MyNetworkManager.startNetwork();
            
            // Waiting 10 000 milliseconds for a rendezvous connection
            Tools.PopInformationMessage(Name, "Waiting maximum 1 minute for a rendezvous connection");
            
            if (MyNetworkManager.waitForRendezvousConnection(60000)) {
            
                // Rendezvous connection established
                Tools.PopInformationMessage(Name, "Rendezvous connection established !!!");
                
                RendezVousService MyRendezVousService = NetPeerGroup.getRendezVousService();
                Enumeration<ID> ConnectedRDVs = MyRendezVousService.getConnectedRendezVous();
                
                while (ConnectedRDVs.hasMoreElements()) {
                    
                    Tools.PopInformationMessage(Name, "Connected to RDV: "
                            + ConnectedRDVs.nextElement().toString());
                    
                }
                
            } else {
                
                // Rendezvous connection not established
                Tools.PopInformationMessage(Name, "Rendezvous connection not established !!!");
                
            }
            
            // Stopping the network
            Tools.PopInformationMessage(Name, "Stopping the JXTA network");
            MyNetworkManager.stopNetwork();
            
        } catch (IOException Ex) {
            
            // Raised when access to local file and directories caused an error
            Tools.PopErrorMessage(Name, Ex.toString());
            
        } catch (PeerGroupException Ex) {
            
            // Raised when the net peer group could not be created
            Tools.PopErrorMessage(Name, Ex.toString());
            
        }

    }

}
