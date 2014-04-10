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

package Examples.C_Peers_And_PeerGroups;

import Examples.Z_Tools_And_Others.Tools;
import java.io.IOException;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.platform.Module;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.ModuleImplAdvertisement;

public class _220_Creating_A_Peer_Group_Example {
    
    public static final String Name = "Example 220";
    
    public static final String MyPeerName = "Santa Claus de la JXTA";
    public static final PeerID MyPeerID = IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID, MyPeerName.getBytes());
    
    public static final String NewPeerGroupName = "Dave Brubeck's Fan Club";
    public static final PeerGroupID NewPeerGroupID = IDFactory.newPeerGroupID(NewPeerGroupName.getBytes());
    
    public static void main(String[] args) {
        
        try {
            
            // Removing any existing configuration
            Tools.DeleteConfigurationInDefaultHome();

            // Creation of the network manager
            NetworkManager MyNetworkManager = new NetworkManager(
                    NetworkManager.ConfigMode.EDGE,
                    Name);
            
            // Setting the peer ID
            Tools.PopInformationMessage(Name, "Setting the peer ID:\n\n" + MyPeerID.toString());
            MyNetworkManager.setPeerID(MyPeerID);
            
            // Starting the JXTA network
            Tools.PopInformationMessage(Name, "Starting the JXTA network");
            PeerGroup TheNetPeerGroup = MyNetworkManager.startNetwork();
            
            // Retrieving a module implementation advertisement
            ModuleImplAdvertisement TheModuleImplementationAdvertisement = 
                    TheNetPeerGroup.getAllPurposePeerGroupImplAdvertisement();
            
            // The creation includes local publishing
            Tools.PopInformationMessage(Name, "Creating the new peer group:\n\n" + NewPeerGroupName);
            PeerGroup MyNewPeerGroup = TheNetPeerGroup.newGroup(
                    NewPeerGroupID,
                    TheModuleImplementationAdvertisement,
                    NewPeerGroupName,
                    "For connoisseurs only..."
                    );
            
            // Don't forget to start your peer group
            if (Module.START_OK == MyNewPeerGroup.startApp(new String[0])) {
                
                Tools.PopInformationMessage(Name, "New peer group started successfully");
                
            } else {
                
                Tools.PopInformationMessage(Name, "New peer group not started successfully !!!");
                
            }
            
            // Displaying IDs
            System.out.println("Net Peer Group ID    : " + PeerGroupID.defaultNetPeerGroupID);
            System.out.println("Santa Claus ID       : " + MyPeerID + "\n");
            System.out.println("New Peer Group ID    : " + MyNewPeerGroup.getPeerGroupID().toString());
            System.out.println("Peer ID in New Group : " + MyNewPeerGroup.getPeerID().toString());
            
            // Stopping the peer group
            Tools.PopInformationMessage(Name, "Stopping the new peer group");
            
            // Stopping the network
            Tools.PopInformationMessage(Name, "Stopping the JXTA network");
            MyNetworkManager.stopNetwork();
            
        } catch (PeerGroupException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        } catch (IOException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        } catch (Exception Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        }

    }
        
}
