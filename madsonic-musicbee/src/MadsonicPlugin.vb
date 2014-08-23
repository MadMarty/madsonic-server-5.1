Imports System.Windows.Forms
Imports System.Drawing
Imports System.ComponentModel
Imports System.Net
Imports System.Web
Imports System.Xml
Imports System.Runtime.InteropServices
Imports System.Threading

Public Class Plugin
    Private mbApiInterface As MusicBeeApiInterface
    Private about As New PluginInfo
    Private host As TextBox
    Private port As TextBox
    Private basePath As TextBox
    Private username As TextBox
    Private password As TextBox
    Private transcode As CheckBox

    Public Function Initialise(ByVal apiInterfacePtr As IntPtr) As PluginInfo
        mbApiInterface = DirectCast(Marshal.PtrToStructure(apiInterfacePtr, GetType(MusicBeeApiInterface)), MusicBeeApiInterface)
        Madsonic.SendNotificationHandler = mbApiInterface.MB_SendNotification
        about.PluginInfoVersion = PluginInfoVersion
        about.Name = "Madsonic"
        about.Description = "Access files and playlists on a Madsonic Server"
        about.Author = "Madevil"
        about.TargetApplication = "Madsonic"
        about.Type = PluginType.Storage

        about.VersionMajor = 1  ' your plugin version
        about.VersionMinor = 1
        about.Revision = 1
        about.MinInterfaceVersion = MinInterfaceVersion
        about.MinApiRevision = MinApiRevision
        about.ReceiveNotifications = ReceiveNotificationFlags.StartupOnly
        about.ConfigurationPanelHeight = 80  ' height in pixels that musicbee should reserve in a panel for config settings. When set, a handle to an empty panel will be passed to the Configure function
        Return about
    End Function

    Public Sub Close(ByVal reason As PluginCloseReason)
        Madsonic.Close()
    End Sub

    Public Function Configure(ByVal panelHandle As IntPtr) As Boolean
        If panelHandle <> IntPtr.Zero Then
            Dim configPanel As Panel = DirectCast(Panel.FromHandle(panelHandle), Panel)
            configPanel.SuspendLayout()
            Dim hostPrompt As New Label
            hostPrompt.AutoSize = True
            hostPrompt.Location = New Point(0, 8)
            hostPrompt.Text = "host:"
            host = New TextBox
            host.Bounds = New Rectangle(80, 5, 120, host.Height)
            host.Text = Madsonic.Host
            Dim portPrompt As New Label
            portPrompt.AutoSize = True
            portPrompt.Location = New Point(212, 8)
            portPrompt.Text = "port:"
            port = New TextBox
            port.Bounds = New Rectangle(250, 5, 32, host.Height)
            port.Text = Madsonic.Port
            Dim basePathPrompt As New Label
            basePathPrompt.AutoSize = True
            basePathPrompt.Location = New Point(297, 8)
            basePathPrompt.Text = "path:"
            basePath = New TextBox
            basePath.Bounds = New Rectangle(337, 5, 70, host.Height)
            basePath.Text = Madsonic.BasePath
            Dim usernamePrompt As New Label
            usernamePrompt.AutoSize = True
            usernamePrompt.Location = New Point(0, 34)
            usernamePrompt.Text = "username:"
            username = New TextBox
            username.Bounds = New Rectangle(80, 31, 120, username.Height)
            username.Text = Madsonic.Username
            Dim passwordPrompt As New Label
            passwordPrompt.AutoSize = True
            passwordPrompt.Location = New Point(0, 60)
            passwordPrompt.Text = "password:"
            password = New TextBox
            password.Bounds = New Rectangle(80, 57, 120, password.Height)
            password.Text = Madsonic.Password
            password.PasswordChar = "*"c
            transcode = New CheckBox
            transcode.AutoSize = True
            transcode.Text = "transcode streams"
            transcode.Checked = Madsonic.Transcode
            configPanel.Controls.AddRange(New Control() {host, hostPrompt, port, portPrompt, basePath, basePathPrompt, username, usernamePrompt, password, passwordPrompt, transcode})
            configPanel.Width = basePath.Right + 10
            transcode.Location = New Point(basePath.Right - TextRenderer.MeasureText(transcode.Text, configPanel.Font).Width - 12, passwordPrompt.Top - 1)
            configPanel.ResumeLayout()
        End If
        Return True
    End Function

    Public Sub ReceiveNotification(ByVal sourceFileUrl As String, ByVal type As NotificationType)
        If type = NotificationType.PluginStartup Then
            Madsonic.CacheUrl = mbApiInterface.Setting_GetPersistentStoragePath() & "\madsonicCache.dat"
            Madsonic.SettingsUrl = mbApiInterface.Setting_GetPersistentStoragePath() & "\madsonicSettings.dat"
            If Madsonic.Initialise() Then
                Madsonic.SendNotificationHandler.Invoke(CallBackType.StorageReady)
            Else
                Madsonic.SendNotificationHandler.Invoke(CallBackType.StorageFailed)
            End If
        End If
    End Sub

    Public Sub SaveSettings()
        If Not Madsonic.SetHost(host.Text.Trim(), port.Text.Trim(), basePath.Text.Trim(), username.Text.Trim(), password.Text.Trim(), transcode.Checked) Then
            Dim message As String = Madsonic.GetError().Message
            If message IsNot Nothing Then
                Windows.Forms.MessageBox.Show(host, "Error: " & message & "     ", "Madsonic Plugin", MessageBoxButtons.OK, MessageBoxIcon.Exclamation)
            End If
        End If
    End Sub

    Public Sub Refresh()
        If Not Madsonic.IsInitialised Then
            If Madsonic.Initialise() Then
                Madsonic.SendNotificationHandler.Invoke(CallbackType.StorageReady)
            Else
                Madsonic.SendNotificationHandler.Invoke(CallbackType.StorageFailed)
            End If
        Else
            Madsonic.Refresh()
        End If
    End Sub

    Public Function IsReady() As Boolean
        Return Madsonic.IsInitialised
    End Function

    Public Function GetIcon() As Bitmap
        Dim resourceManager As New System.Resources.ResourceManager("MusicBeePlugin.Images", System.Reflection.Assembly.GetExecutingAssembly())
        Return DirectCast(resourceManager.GetObject("Madsonic"), Bitmap)
    End Function

    Public Function FolderExists(ByVal path As String) As Boolean
        Return Madsonic.FolderExists(path)
    End Function

    Public Function GetFolders(ByVal path As String) As String()
        Return Madsonic.GetFolders(path)
    End Function

    Public Function GetFiles(ByVal path As String) As KeyValuePair(Of Byte, String)()()
        Return Madsonic.GetFiles(path)
    End Function

    Public Function FileExists(ByVal url As String) As Boolean
        Return Madsonic.FileExists(url)
    End Function

    Public Function GetFile(ByVal url As String) As KeyValuePair(Of Byte, String)()
        Return Madsonic.GetFile(url)
    End Function

    Public Function GetFileArtwork(ByVal url As String) As Byte()
        Return Madsonic.GetFileArtwork(url)
    End Function

    Public Function GetPlaylists() As KeyValuePair(Of String, String)()
        Return Madsonic.GetPlaylists()
    End Function

    Public Function GetPlaylistFiles(ByVal id As String) As KeyValuePair(Of Byte, String)()()
        Return Madsonic.GetPlaylistFiles(id)
    End Function

    Public Function GetStream(ByVal url As String) As IO.Stream
        Return Madsonic.GetStream(url)
    End Function

    Public Function GetError() As Exception
        Return Madsonic.GetError()
    End Function

    Private Class Madsonic
        Public Shared Host As String = "localhost"
        Public Shared Port As String = "4040"
        Public Shared BasePath As String = "/"
        Public Shared Username As String = "admin"
        Public Shared Password As String = "admin"
        Public Shared Transcode As Boolean = False
        Public Shared IsInitialised As Boolean = False
        Public Shared SettingsUrl As String
        Public Shared CacheUrl As String
        Public Shared SendNotificationHandler As Plugin.MB_SendNotificationDelegate
        Private Const TagCount As Integer = 10
        Private Shared serverName As String
        Private Shared lastEx As Exception = Nothing
        Private Shared cacheFileLock As New Object
        Private Shared cacheLock As New Object
        Private Shared cachedFiles()() As KeyValuePair(Of Byte, String) = Nothing
        Private Shared retrieveThread As Thread = Nothing
        Private Shared collectionNames() As String
        Private Shared lastModified As New Dictionary(Of String, ULong)(StringComparer.Ordinal)
        Private Shared folderLookupLock As New Object
        Private Shared folderLookup As New Dictionary(Of String, String)(StringComparer.Ordinal)

        Public Shared Function Initialise() As Boolean
            lastEx = Nothing
            Try
                If IO.File.Exists(SettingsUrl) Then
                    Using reader As New IO.StreamReader(SettingsUrl)
                        Host = reader.ReadLine()
                        Port = reader.ReadLine()
                        BasePath = reader.ReadLine()
                        Username = reader.ReadLine()
                        Password = reader.ReadLine()
                        Transcode = (reader.ReadLine() = "Y")
                    End Using
                End If
                IsInitialised = SetServerName()
            Catch ex As Exception
                lastEx = ex
                IsInitialised = False
            End Try
            Return IsInitialised
        End Function

        Private Shared Function SetServerName() As Boolean
            Dim isPingOk As Boolean = False
            Dim xml As String
            Try
                Dim httpRequest As System.Net.HttpWebRequest = DirectCast(System.Net.WebRequest.Create("http://" & Host & ":" & Port & BasePath), HttpWebRequest)
                httpRequest.Method = "GET"
                httpRequest.Timeout = 5000
                Dim responseHost As String = DirectCast(httpRequest.GetResponse(), HttpWebResponse).ResponseUri.Host
                serverName = "http://" & responseHost & ":" & Port & BasePath
                xml = GetHttpRequestXml("ping.view", Nothing, 5000)
                isPingOk = (xml.IndexOf("status=""ok""", StringComparison.Ordinal) <> -1)
                If isPingOk Then
                    Return True
                ElseIf String.Compare(responseHost, Host, StringComparison.OrdinalIgnoreCase) = 0 Then
                    lastEx = New IO.IOException(GetErrorMessage(xml))
                    Return False
                End If
            Catch
            End Try
            Try
                serverName = "http://" & Host & ":" & Port & BasePath
                xml = GetHttpRequestXml("ping.view", Nothing, 5000)
                isPingOk = (xml.IndexOf("status=""ok""", StringComparison.Ordinal) <> -1)
                If Not isPingOk Then
                    lastEx = New IO.IOException(GetErrorMessage(xml))
                End If
                Return isPingOk
            Catch ex As Exception
                lastEx = ex
                Return False
            End Try
        End Function

        Public Shared Sub Close()
            If retrieveThread IsNot Nothing AndAlso retrieveThread.IsAlive Then
                retrieveThread.Abort()
                retrieveThread = Nothing
            End If
        End Sub

        Public Shared Function SetHost(ByVal host As String, ByVal port As String, ByVal basePath As String, ByVal username As String, ByVal password As String, ByVal transcode As Boolean) As Boolean
            lastEx = Nothing
            Try
                host = host.Trim()
                If host.StartsWith("http://", StringComparison.OrdinalIgnoreCase) Then
                    host = host.Substring(7)
                End If
                port = port.Trim()
                basePath = basePath.Trim()
                If Not basePath.EndsWith("/"c) Then
                    basePath &= "/"c
                End If
                Dim isChanged As Boolean = (host <> Madsonic.Host OrElse port <> Madsonic.Port OrElse basePath <> Madsonic.BasePath OrElse username <> Madsonic.Username OrElse password <> Madsonic.Password)
                If isChanged Then
                    Dim savedHost As String = Madsonic.Host
                    Dim savedport As String = Madsonic.Port
                    Dim savedBasePath As String = Madsonic.BasePath
                    Dim savedUsername As String = Madsonic.Username
                    Dim savedPassword As String = Madsonic.Password
                    Dim isPingOk As Boolean
                    Try
                        Madsonic.Host = host
                        Madsonic.Port = port
                        Madsonic.BasePath = basePath
                        Madsonic.Username = username
                        Madsonic.Password = password
                        isPingOk = SetServerName()
                    Catch
                        isPingOk = False
                    End Try
                    If Not isPingOk Then
                        Madsonic.Host = savedHost
                        Madsonic.Port = savedport
                        Madsonic.BasePath = savedBasePath
                        Madsonic.Username = savedUsername
                        Madsonic.Password = savedPassword
                        Return False
                    End If
                    IsInitialised = True
                End If
                isChanged = (isChanged OrElse transcode <> Madsonic.Transcode)
                If Not isChanged Then Return True
                Using writer As New IO.StreamWriter(SettingsUrl)
                    writer.WriteLine(host)
                    writer.WriteLine(port)
                    writer.WriteLine(basePath)
                    writer.WriteLine(username)
                    writer.WriteLine(password)
                    writer.WriteLine(If(transcode, "Y", "N"))
                End Using
                Madsonic.Transcode = transcode
                Try
                    SendNotificationHandler.Invoke(CallbackType.SettingsUpdated)
                Catch
                End Try
                Return True
            Catch ex As Exception
                lastEx = ex
                Return False
            End Try
        End Function

        Private Shared Function GetErrorMessage(ByVal xml As String) As String
            Dim startIndex As Integer = xml.IndexOf("message=", StringComparison.Ordinal)
            If startIndex = -1 Then
                Return "Unknown error"
            Else
                Dim endIndex As Integer = xml.IndexOf("""/>", startIndex)
                If endIndex = -1 Then
                    Return xml.Substring(startIndex + 9)
                Else
                    Return xml.Substring(startIndex + 9, endIndex - startIndex - 10)
                End If
            End If
        End Function

        Public Shared Sub Refresh()
        End Sub

        Public Shared Function FolderExists(ByVal path As String) As Boolean
            Return (String.IsNullOrEmpty(path) OrElse path = "\"c OrElse GetFolderId(path) IsNot Nothing)
        End Function

        Public Shared Function GetFolders(ByVal path As String) As String()
            lastEx = Nothing
            Dim folders() As String
            If Not IsInitialised Then
                folders = New String() {}
            ElseIf String.IsNullOrEmpty(path) Then
                Dim list As New List(Of String)
                For Each folder As KeyValuePair(Of String, String) In GetRootFolders(True, True, False)
                    list.Add(folder.Value)
                Next folder
                folders = list.ToArray()
            ElseIf path.IndexOf("\"c) = path.LastIndexOf("\"c) Then
                Dim list As New List(Of String)
                Dim folderId As String = GetFolderId(path)
                If folderId = Nothing Then
                    folders = New String() {}
                Else
                    For Each folder As KeyValuePair(Of String, String) In GetRootFolders(folderId, path.Substring(0, path.Length - 1), False, False, False)
                        list.Add(folder.Key)
                    Next folder
                End If
                folders = list.ToArray()
            Else
                If Not path.EndsWith("\"c) Then
                    path &= "\"c
                End If
                Dim folderId As String = GetFolderId(path)
                If folderId = Nothing Then
                    folders = New String() {}
                Else
                    Using stream As IO.Stream = GetHttpRequestStream("getMusicDirectory.view", "id=" & folderId), _
                          xmlReader As New Xml.XmlTextReader(stream)
                        Dim list As New List(Of String)
                        Do While xmlReader.Read()
                            If xmlReader.NodeType = XmlNodeType.Element AndAlso String.Compare(xmlReader.Name, "child", StringComparison.Ordinal) = 0 Then
                                If String.Compare(xmlReader.GetAttribute("isDir"), "true", StringComparison.Ordinal) = 0 Then
                                    folderId = xmlReader.GetAttribute("id")
                                    Dim folderName As String = path & xmlReader.GetAttribute("title")
                                    list.Add(folderName)
                                    If Not folderLookup.ContainsKey(folderName) Then
                                        folderLookup.Add(folderName, folderId)
                                    End If
                                End If
                            End If
                        Loop
                        xmlReader.Close()
                        folders = list.ToArray()
                    End Using
                End If
            End If
            Return folders
        End Function

        Public Shared Function GetFiles(ByVal path As String) As KeyValuePair(Of Byte, String)()()
            Dim threadStarted As Boolean = False
            lastEx = Nothing
            Dim files()() As KeyValuePair(Of Byte, String)
            If Not IsInitialised Then
                files = New KeyValuePair(Of Byte, String)()() {}
            Else
                Dim cacheLoaded As Boolean = (cachedFiles IsNot Nothing)
                If Not cacheLoaded AndAlso Not IO.File.Exists(CacheUrl) Then
                    files = Nothing
                Else
                    ' load and return cache
                    files = GetCachedFiles()
                End If
                Dim cacheUpdating As Boolean = (retrieveThread IsNot Nothing)
                If Not cacheUpdating AndAlso (String.IsNullOrEmpty(path) OrElse Not cacheLoaded) Then
                    threadStarted = True
                    retrieveThread = New Thread(AddressOf ExecuteGetFolderFiles)
                    retrieveThread.IsBackground = True
                    retrieveThread.Start()
                End If
                If Not String.IsNullOrEmpty(path) Then
                    If Not cacheLoaded OrElse cacheUpdating OrElse files Is Nothing Then
                        Return GetFolderFiles(path)
                    Else
                        files = GetPathFilteredFiles(files, path)
                    End If
                End If
            End If
            If Not threadStarted Then
                Try
                    SendNotificationHandler.Invoke(CallbackType.FilesRetrievedNoChange)
                Catch
                End Try
            End If
            Return files
        End Function

        Private Shared Function GetCachedFiles() As KeyValuePair(Of Byte, String)()()
            If cachedFiles Is Nothing Then
                Dim files()() As KeyValuePair(Of Byte, String)
                SyncLock cacheFileLock
                    Using stream As New IO.FileStream(CacheUrl, IO.FileMode.Open, IO.FileAccess.Read, IO.FileShare.Read, 4096, IO.FileOptions.SequentialScan), _
                          reader As New IO.BinaryReader(stream)
                        ' ignore version for now
                        Dim version As Integer = reader.ReadInt32()
                        Dim count As Integer = reader.ReadInt32()
                        files = New KeyValuePair(Of Byte, String)(count - 1)() {}
                        For index As Integer = 0 To count - 1
                            Dim tags() As KeyValuePair(Of Byte, String) = New KeyValuePair(Of Byte, String)(TagCount) {}
                            For tagIndex As Integer = 0 To TagCount
                                Dim tagType As Byte = reader.ReadByte()
                                tags(tagIndex) = New KeyValuePair(Of Byte, String)(tagType, reader.ReadString())
                            Next tagIndex
                            files(index) = tags
                        Next index
                        If version = 2 Then
                            count = reader.ReadInt32()
                            For index As Integer = 1 To count
                                Dim collectionName As String = reader.ReadString()
                                If Not lastModified.ContainsKey(collectionName) Then
                                    lastModified.Add(collectionName, reader.ReadUInt64())
                                End If
                            Next index
                        End If
                        reader.Close()
                    End Using
                End SyncLock
                SyncLock cacheLock
                    If cachedFiles Is Nothing Then
                        cachedFiles = files
                    End If
                End SyncLock
            End If
            Return cachedFiles
        End Function

        Private Shared Function GetPathFilteredFiles(ByVal files()() As KeyValuePair(Of Byte, String), ByVal path As String) As KeyValuePair(Of Byte, String)()()
            Dim filteredFiles As New List(Of KeyValuePair(Of Byte, String)())
            If Not path.EndsWith("\"c) Then
                path &= "\"c
            End If
            For index As Integer = 0 To files.Length - 1
                If files(index)(0).Value.StartsWith(path) Then
                    filteredFiles.Add(files(index))
                End If
            Next index
            files = filteredFiles.ToArray()
            Array.Sort(Of KeyValuePair(Of Byte, String)())(files, New FileSorter)
            Return files
        End Function

        Private Shared Sub ExecuteGetFolderFiles()
            Try
                Dim files()() As KeyValuePair(Of Byte, String)
                Dim list As New List(Of KeyValuePair(Of Byte, String)())
                Dim folders As List(Of KeyValuePair(Of String, String)) = GetRootFolders(False, True, True)
                Dim anyChanges As Boolean
                If folders Is Nothing Then
                    anyChanges = False
                Else
                    For Each folder As KeyValuePair(Of String, String) In folders
                        GetFolderFiles(folder.Value, folder.Key, list)
                    Next
                    files = list.ToArray()
                    Dim oldCachedFiles()() As KeyValuePair(Of Byte, String)
                    SyncLock cacheLock
                        oldCachedFiles = cachedFiles
                        cachedFiles = files
                    End SyncLock
                    anyChanges = (oldCachedFiles Is Nothing OrElse cachedFiles.Length <> oldCachedFiles.Length)
                    If Not anyChanges Then
                        For index As Integer = 0 To cachedFiles.Length - 1
                            Dim tags1() As KeyValuePair(Of Byte, String) = cachedFiles(index)
                            Dim tags2() As KeyValuePair(Of Byte, String) = oldCachedFiles(index)
                            For tagIndex As Integer = 0 To TagCount - 1
                                If String.Compare(tags1(tagIndex).Value, tags2(tagIndex).Value, StringComparison.Ordinal) <> 0 Then
                                    anyChanges = True
                                    Exit For
                                End If
                            Next tagIndex
                        Next index
                    End If
                End If
                If Not anyChanges Then
                    Try
                        SendNotificationHandler.Invoke(CallbackType.FilesRetrievedNoChange)
                    Catch
                    End Try
                Else
                    Try
                        SendNotificationHandler.Invoke(CallbackType.FilesRetrievedChanged)
                    Catch
                    End Try
                    Try
                        SyncLock cacheFileLock
                            Using stream As New IO.FileStream(CacheUrl, IO.FileMode.Create, IO.FileAccess.Write, IO.FileShare.None), _
                                  writer As New IO.BinaryWriter(stream)
                                writer.Write(2)  ' version
                                writer.Write(files.Length)
                                For index As Integer = 0 To files.Length - 1
                                    Dim tags() As KeyValuePair(Of Byte, String) = files(index)
                                    For tagIndex As Integer = 0 To TagCount
                                        Dim tag As KeyValuePair(Of Byte, String) = tags(tagIndex)
                                        writer.Write(tag.Key)
                                        writer.Write(tag.Value)
                                    Next tagIndex
                                Next index
                                writer.Write(lastModified.Count)
                                For Each item As KeyValuePair(Of String, ULong) In lastModified
                                    writer.Write(item.Key)
                                    writer.Write(item.Value)
                                Next item
                                writer.Close()
                            End Using
                        End SyncLock
                    Catch
                    End Try
                End If
            Catch ex As Exception
                lastEx = ex
                Try
                    SendNotificationHandler.Invoke(CallbackType.FilesRetrievedFail)
                Catch
                End Try
            Finally
                retrieveThread = Nothing
            End Try
        End Sub

        Private Shared Function GetRootFolders(ByVal collectionOnly As Boolean, ByVal refresh As Boolean, ByVal dirtyOnly As Boolean) As List(Of KeyValuePair(Of String, String))
            Dim folders As List(Of KeyValuePair(Of String, String)) = Nothing
            SyncLock folderLookupLock
                If refresh OrElse folderLookup.Count = 0 Then
                    folders = New List(Of KeyValuePair(Of String, String))
                    Dim collection As New List(Of KeyValuePair(Of String, String))
                    Using stream As IO.Stream = GetHttpRequestStream("getMusicFolders.view", Nothing), _
                          xmlReader As New Xml.XmlTextReader(stream)
                        Do While xmlReader.Read()
                            If xmlReader.NodeType = XmlNodeType.Element AndAlso String.Compare(xmlReader.Name, "musicFolder", StringComparison.Ordinal) = 0 Then
                                Dim folderId As String = xmlReader.GetAttribute("id")
                                Dim folderName As String = xmlReader.GetAttribute("name")
                                If folderLookup.ContainsKey(folderName) Then
                                    folderLookup(folderName) = folderId
                                Else
                                    folderLookup.Add(folderName, folderId)
                                End If
                                collection.Add(New KeyValuePair(Of String, String)(folderId, folderName))
                            End If
                        Loop
                    End Using
                    collectionNames = New String(collection.Count - 1) {}
                    For index As Integer = 0 To collection.Count - 1
                        collectionNames(index) = collection(index).Value & "\"
                    Next index
                    Dim isDirty As Boolean = False
                    For Each item As KeyValuePair(Of String, String) In collection
                        folders.AddRange(GetRootFolders(item.Key, item.Value, True, (refresh AndAlso dirtyOnly), isDirty))
                    Next item
                    If collectionOnly Then
                        Return collection
                    ElseIf dirtyOnly AndAlso Not isDirty Then
                        Return Nothing
                    End If
                End If
            End SyncLock
            Return folders
        End Function

        Private Shared Function GetRootFolders(ByVal collectionId As String, ByVal collectionName As String, ByVal indices As Boolean, ByVal updateIsDirty As Boolean, ByRef isDirty As Boolean) As List(Of KeyValuePair(Of String, String))
            Dim folders As New List(Of KeyValuePair(Of String, String))
            Using stream As IO.Stream = GetHttpRequestStream("getIndexes.view", "musicFolderId=" & collectionId), _
                  xmlReader As New Xml.XmlTextReader(stream)
                Do While xmlReader.Read()
                    If xmlReader.NodeType = XmlNodeType.Element Then
                        If String.Compare(xmlReader.Name, "artist", StringComparison.Ordinal) = 0 Then
                            Dim folderId As String = xmlReader.GetAttribute("id")
                            Dim folderName As String = collectionName & "\" & xmlReader.GetAttribute("name")
                            If folderLookup.ContainsKey(folderName) Then
                                folderLookup(folderName) = folderId
                            Else
                                folderLookup.Add(folderName, folderId)
                            End If
                            folders.Add(New KeyValuePair(Of String, String)(If(indices, folderId, folderName), collectionName))
                        ElseIf updateIsDirty AndAlso String.Compare(xmlReader.Name, "indexes", StringComparison.Ordinal) = 0 Then
                            Dim serverLastModified As ULong
                            If ULong.TryParse(xmlReader.GetAttribute("lastModified"), serverLastModified) Then
                                Dim clientLastModified As ULong
                                If Not lastModified.TryGetValue(collectionName, clientLastModified) Then
                                    isDirty = True
                                    lastModified.Add(collectionName, serverLastModified)
                                ElseIf serverLastModified > clientLastModified Then
                                    isDirty = True
                                    lastModified(collectionName) = serverLastModified
                                End If
                            End If
                        End If
                    End If
                Loop
            End Using
            Return folders
        End Function

        Private Shared Sub GetFolderFiles(ByVal baseFolderName As String, ByVal folderId As String, ByVal files As List(Of KeyValuePair(Of Byte, String)()))
            Using stream As IO.Stream = GetHttpRequestStream("getMusicDirectory.view", "id=" & folderId), _
                  xmlReader As New Xml.XmlTextReader(stream)
                Do While xmlReader.Read()
                    If xmlReader.NodeType = XmlNodeType.Element AndAlso String.Compare(xmlReader.Name, "child", StringComparison.Ordinal) = 0 Then
                        If String.Compare(xmlReader.GetAttribute("isDir"), "true", StringComparison.Ordinal) = 0 Then
                            GetFolderFiles(baseFolderName, xmlReader.GetAttribute("id"), files)
                        Else
                            Dim tags() As KeyValuePair(Of Byte, String) = GetTags(xmlReader, baseFolderName)
                            If tags IsNot Nothing Then
                                files.Add(tags)
                            End If
                        End If
                    End If
                Loop
                xmlReader.Close()
            End Using
        End Sub

        Private Shared Function GetFolderFiles(ByVal path As String) As KeyValuePair(Of Byte, String)()()
            If Not path.EndsWith("\"c) Then
                path &= "\"
            End If
            Dim folderId As String = GetFolderId(path)
            Dim files As New List(Of KeyValuePair(Of Byte, String)())
            If folderId Is Nothing Then
                Return New KeyValuePair(Of Byte, String)()() {}
            Else
                GetFolderFiles(path.Substring(0, path.IndexOf("\"c)), folderId, files)
            End If
            Return files.ToArray()
        End Function

        Private Shared Function GetFolderId(ByVal url As String) As String
            Dim charIndex As Integer = url.LastIndexOf("\"c)
            If charIndex = -1 Then Throw New ArgumentException
            If folderLookup.Count = 0 Then GetRootFolders(False, False, False)
            Dim folderId As String = Nothing
            If Not folderLookup.TryGetValue(url.Substring(0, charIndex), folderId) Then
                Dim sectionStartIndex As Integer = url.IndexOf("\"c) + 1
                charIndex = url.IndexOf("\"c, sectionStartIndex)
                If charIndex = -1 Then Throw New ArgumentException
                Do While charIndex <> -1
                    Dim subFolderId As String
                    Dim subFolderPath As String

                    subFolderPath = url.Substring(0, charIndex)

                    If folderLookup.TryGetValue(url.Substring(0, charIndex), subFolderId) Then
                        folderId = subFolderId
                    Else
                        Dim folderName As String = url.Substring(sectionStartIndex, charIndex - sectionStartIndex)
                        Using stream As IO.Stream = GetHttpRequestStream("getMusicDirectory.view", "id=" & folderId), _
                              xmlReader As New Xml.XmlTextReader(stream)
                            Do While xmlReader.Read()
                                If xmlReader.NodeType = XmlNodeType.Element AndAlso String.Compare(xmlReader.Name, "child", StringComparison.Ordinal) = 0 Then
                                    If String.Compare(xmlReader.GetAttribute("isDir"), "true", StringComparison.Ordinal) = 0 AndAlso String.Compare(xmlReader.GetAttribute("path"), folderName, StringComparison.Ordinal) = 0 Then
                                        folderId = xmlReader.GetAttribute("id")
                                        folderLookup.Add(url.Substring(0, charIndex), folderId)
                                        Exit Do
                                    End If
                                End If
                            Loop
                            xmlReader.Close()
                        End Using
                    End If
                    sectionStartIndex = charIndex + 1
                    charIndex = url.IndexOf("\"c, sectionStartIndex)
                Loop
            End If
            Return folderId
        End Function

        Private Shared Function GetTranslatedUrl(ByVal url As String) As String
            Return url.Replace("\"c, "/"c)
        End Function

        Private Shared Function GetParentId(ByVal url As String) As String
            Dim folderId As String = GetFolderId(url)
            If folderId IsNot Nothing Then
                Using stream As IO.Stream = GetHttpRequestStream("getMusicDirectory.view", "id=" & folderId), _
                      xmlReader As New Xml.XmlTextReader(stream)
                    Dim filePath As String = GetTranslatedUrl(url.Substring(url.IndexOf("\"c) + 1))
                    Do While xmlReader.Read()
                        If xmlReader.NodeType = XmlNodeType.Element AndAlso String.Compare(xmlReader.Name, "child", StringComparison.Ordinal) = 0 Then
                            If String.Compare(xmlReader.GetAttribute("path"), filePath, StringComparison.Ordinal) = 0 Then
                                Return xmlReader.GetAttribute("id")
                            End If
                        End If
                    Loop
                    xmlReader.Close()
                End Using
            End If
            Return Nothing
        End Function


        Private Shared Function GetFileId(ByVal url As String) As String
            Dim folderId As String = GetFolderId(url)
            If folderId IsNot Nothing Then
                Using stream As IO.Stream = GetHttpRequestStream("getMusicDirectory.view", "id=" & folderId), _
                      xmlReader As New Xml.XmlTextReader(stream)
                    Dim filePath As String = GetTranslatedUrl(url.Substring(url.IndexOf("\"c) + 1))
                    Do While xmlReader.Read()
                        If xmlReader.NodeType = XmlNodeType.Element AndAlso String.Compare(xmlReader.Name, "child", StringComparison.Ordinal) = 0 Then
                            If String.Compare(xmlReader.GetAttribute("path"), filePath, StringComparison.Ordinal) = 0 Then
                                Return xmlReader.GetAttribute("id")
                            End If
                        End If
                    Loop
                    xmlReader.Close()
                End Using
            End If
            Return Nothing
        End Function

        Private Shared Function GetResolvedUrl(ByVal url As String) As String
            If folderLookup.Count = 0 Then GetRootFolders(False, False, False)
            If collectionNames.Length = 1 Then
                Return collectionNames(0) & url
            Else
                Dim path As String = url.Substring(0, url.LastIndexOf("\"c))
                Dim lastMatch As String
                Dim count As Integer = 0
                For index As Integer = 0 To collectionNames.Length - 1
                    If GetFolderId(collectionNames(index) & path) IsNot Nothing Then
                        count += 1
                        lastMatch = collectionNames(index) & url
                    End If
                Next index
                If count = 1 Then
                    Return lastMatch
                Else
                    For index As Integer = 0 To collectionNames.Length - 1
                        If GetFolderId(collectionNames(index) & path) IsNot Nothing Then
                            lastMatch = collectionNames(index) & url
                            If GetFileId(lastMatch) IsNot Nothing Then
                                Return lastMatch
                            End If
                        End If
                    Next index
                End If
            End If
            Return url
        End Function

        Private Shared Function GetCoverArtId(ByVal url As String) As String
            Dim folderId As String = GetFolderId(url)
            If folderId IsNot Nothing Then
                Using stream As IO.Stream = GetHttpRequestStream("getMusicDirectory.view", "id=" & folderId), _
                      xmlReader As New Xml.XmlTextReader(stream)
                    Dim filePath As String = GetTranslatedUrl(url.Substring(url.IndexOf("\"c) + 1))
                    Do While xmlReader.Read()
                        If xmlReader.NodeType = XmlNodeType.Element AndAlso String.Compare(xmlReader.Name, "child", StringComparison.Ordinal) = 0 Then
                            If String.Compare(xmlReader.GetAttribute("path"), filePath, StringComparison.Ordinal) = 0 Then
                                Return xmlReader.GetAttribute("coverArt")
                            End If
                        End If
                    Loop
                    xmlReader.Close()
                End Using
            End If
            Return Nothing
        End Function

        Public Shared Function FileExists(ByVal url As String) As Boolean
            Return (GetFileId(url) IsNot Nothing)
        End Function

        Public Shared Function GetFile(ByVal url As String) As KeyValuePair(Of Byte, String)()
            Dim folderId As String = GetFolderId(url)
            If folderId IsNot Nothing Then
                Using stream As IO.Stream = GetHttpRequestStream("getMusicDirectory.view", "id=" & folderId), _
                      xmlReader As New Xml.XmlTextReader(stream)
                    Dim filePath As String = GetTranslatedUrl(url.Substring(url.IndexOf("\"c) + 1))
                    Do While xmlReader.Read()
                        If xmlReader.NodeType = XmlNodeType.Element AndAlso String.Compare(xmlReader.Name, "child", StringComparison.Ordinal) = 0 Then
                            If String.Compare(xmlReader.GetAttribute("path"), filePath, StringComparison.Ordinal) = 0 Then
                                Return GetTags(xmlReader, Nothing)
                            End If
                        End If
                    Loop
                    xmlReader.Close()
                End Using
            End If
            Return Nothing
        End Function

        Private Shared Function GetTags(ByVal xmlReader As Xml.XmlTextReader, ByVal baseFolderName As String) As KeyValuePair(Of Byte, String)()
            If String.Compare(xmlReader.GetAttribute("isVideo"), "true", StringComparison.Ordinal) = 0 Then
                Return Nothing
            Else
                Dim tags(TagCount) As KeyValuePair(Of Byte, String)
                Dim path As String = xmlReader.GetAttribute("path").Replace("/"c, "\"c)
                If baseFolderName Is Nothing Then
                    path = GetResolvedUrl(path)
                Else
                    path = baseFolderName & "\" & path
                End If
                tags(0) = New KeyValuePair(Of Byte, String)(CByte(FilePropertyType.Url), path)
                tags(1) = New KeyValuePair(Of Byte, String)(CByte(MetaDataType.Artist), xmlReader.GetAttribute("artist"))
                tags(2) = New KeyValuePair(Of Byte, String)(CByte(MetaDataType.TrackTitle), xmlReader.GetAttribute("title"))
                tags(3) = New KeyValuePair(Of Byte, String)(CByte(MetaDataType.Album), xmlReader.GetAttribute("album"))
                tags(4) = New KeyValuePair(Of Byte, String)(CByte(MetaDataType.Year), xmlReader.GetAttribute("year"))
                tags(5) = New KeyValuePair(Of Byte, String)(CByte(MetaDataType.TrackNo), xmlReader.GetAttribute("track"))
                tags(6) = New KeyValuePair(Of Byte, String)(CByte(MetaDataType.Genre), xmlReader.GetAttribute("genre"))
                Dim duration As Integer
                If Integer.TryParse(xmlReader.GetAttribute("duration"), duration) Then
                    tags(7) = New KeyValuePair(Of Byte, String)(CByte(FilePropertyType.Duration), (duration * 1000).ToString())
                End If
                tags(8) = New KeyValuePair(Of Byte, String)(CByte(FilePropertyType.Bitrate), xmlReader.GetAttribute("bitRate"))
                tags(9) = New KeyValuePair(Of Byte, String)(CByte(FilePropertyType.Size), xmlReader.GetAttribute("size"))
                tags(10) = New KeyValuePair(Of Byte, String)(CByte(MetaDataType.Artwork), If(String.IsNullOrEmpty(xmlReader.GetAttribute("coverArt")), "", "Y"))
                For tagIndex As Integer = 1 To TagCount - 2
                    If tags(tagIndex).Value Is Nothing Then
                        tags(tagIndex) = New KeyValuePair(Of Byte, String)(tags(tagIndex).Key, "")
                    End If
                Next tagIndex
                Return tags
            End If
        End Function

        Public Shared Function GetFileArtwork(ByVal url As String) As Byte()
            lastEx = Nothing
            Dim bytes() As Byte = Nothing
            Try
                Dim id As String = GetCoverArtId(url)
                If id IsNot Nothing Then
                    Using stream As ConnectStream = GetHttpRequestStream("getCoverArt.view", "id=" & id)
                        If String.Compare(stream.ContentType, "text/xml", StringComparison.Ordinal) <> 0 Then
                            bytes = stream.ToArray()
                        Else
                            lastEx = New IO.InvalidDataException(GetErrorMessage(System.Text.Encoding.UTF8.GetString(stream.ToArray())))
                        End If
                    End Using
                End If
            Catch ex As Exception
                lastEx = ex
            End Try
            Return bytes
        End Function

        Public Shared Function GetPlaylists() As KeyValuePair(Of String, String)()
            lastEx = Nothing
            Dim playlists As New List(Of KeyValuePair(Of String, String))
            Using stream As IO.Stream = GetHttpRequestStream("getPlaylists.view", Nothing), _
                  xmlReader As New Xml.XmlTextReader(stream)
                Do While xmlReader.Read()
                    If xmlReader.NodeType = XmlNodeType.Element AndAlso String.Compare(xmlReader.Name, "playlist", StringComparison.Ordinal) = 0 Then
                        playlists.Add(New KeyValuePair(Of String, String)(xmlReader.GetAttribute("id"), xmlReader.GetAttribute("name")))
                    End If
                Loop
            End Using
            Return playlists.ToArray()
        End Function

        Public Shared Function GetPlaylistFiles(ByVal id As String) As KeyValuePair(Of Byte, String)()()
            lastEx = Nothing
            Using stream As IO.Stream = GetHttpRequestStream("getPlaylist.view", "id=" & id), _
                  xmlReader As New Xml.XmlTextReader(stream)
                Dim files As New List(Of KeyValuePair(Of Byte, String)())
                Do While xmlReader.Read()
                    If xmlReader.NodeType = XmlNodeType.Element AndAlso String.Compare(xmlReader.Name, "entry", StringComparison.Ordinal) = 0 Then
                        Dim tags() As KeyValuePair(Of Byte, String) = GetTags(xmlReader, Nothing)
                        If tags IsNot Nothing Then
                            files.Add(tags)
                        End If
                    End If
                Loop
                xmlReader.Close()
                Return files.ToArray()
            End Using
        End Function

        Public Shared Function CreatePlaylist(ByVal name As String) As String
            lastEx = Nothing
        End Function

        Public Shared Function UpdatePlaylist(ByVal id As String, ByVal files()() As KeyValuePair(Of Byte, String)) As Boolean
            lastEx = Nothing
            Dim fileIds As New List(Of String)
            For index As Integer = 0 To files.Length - 1
                fileIds.Add(GetFileId(files(index)(0).Value))
            Next index
            Dim ids As New System.Text.StringBuilder(4096)
            For Each fileId As String In fileIds
                ids.Append("&songId=")
                ids.Append(fileId)
            Next fileId
            '  Dim xml As String = GetHttpRequestXml("createPlaylist.view", "playlistId=" & id & ids.ToString())
        End Function

        Public Shared Function DeletePlaylist(ByVal id As String) As Boolean
            lastEx = Nothing
        End Function

        Public Shared Function GetStream(ByVal url As String) As IO.Stream
            lastEx = Nothing
            Dim id As String = GetFileId(url)
            If id Is Nothing Then
                lastEx = New IO.FileNotFoundException
            Else
                Dim stream As ConnectStream = GetHttpRequestStream(If(Transcode, "stream.view", "download.view"), "id=" & id)
                If String.Compare(stream.ContentType, "text/xml", StringComparison.Ordinal) <> 0 Then
                    Return stream
                Else
                    Using stream
                        lastEx = New IO.InvalidDataException(GetErrorMessage(System.Text.Encoding.UTF8.GetString(stream.ToArray())))
                    End Using
                End If
            End If
            Return Nothing
        End Function

        'Public Shared Function GetStreamUrl(ByVal url As String) As String
        '    lastEx = Nothing
        '    Dim id As String = GetFileId(url)
        '    If id Is Nothing Then
        '        Return Nothing
        '    Else
        '        Return "http://localhost/stream?player=2&pathUtf8Hex=" & id & "&suffix=" & url.Substring(url.LastIndexOf("."c))
        '    End If
        'End Function

        Public Shared Function GetError() As Exception
            Return lastEx
        End Function

        Private Shared Function GetHttpRequestXml(ByVal query As String, ByVal parameters As String, ByVal timeout As Integer) As String
            Using stream As ConnectStream = GetHttpRequestStream(query, parameters, timeout)
                Return System.Text.Encoding.UTF8.GetString(stream.ToArray())
            End Using
        End Function

        Private Shared Function GetHttpRequestStream(ByVal query As String, ByVal parameters As String, Optional ByVal timeout As Integer = 30000) As ConnectStream
            Return New ConnectStream(serverName & "rest/" & query & "?u=" & Username & "&p=" & Password & "&v=1.9.0&c=MusicBee" & If(String.IsNullOrEmpty(parameters), "", "&" & parameters), timeout)
        End Function

        Private NotInheritable Class FileSorter
            Inherits Comparer(Of KeyValuePair(Of Byte, String)())
            Public Overrides Function Compare(ByVal tags1() As KeyValuePair(Of Byte, String), ByVal tags2() As KeyValuePair(Of Byte, String)) As Integer
                Return String.Compare(tags1(0).Value, tags2(0).Value, StringComparison.OrdinalIgnoreCase)
            End Function
        End Class  ' FileSorter

        Private NotInheritable Class ConnectStream
            Inherits IO.Stream
            Public ContentType As String
            Private streamLength As Long
            Private webResponse As HttpWebResponse
            Private responseStream As IO.Stream

            Public Sub New(ByVal url As String, ByVal timeout As Integer)
                Dim httpRequest As System.Net.HttpWebRequest = DirectCast(System.Net.WebRequest.Create(url), HttpWebRequest)
                httpRequest.Accept = "*/*"
                httpRequest.Method = "GET"
                httpRequest.Timeout = timeout
                webResponse = DirectCast(httpRequest.GetResponse(), HttpWebResponse)
                ContentType = webResponse.ContentType
                streamLength = webResponse.ContentLength
                responseStream = webResponse.GetResponseStream()
            End Sub

            Protected Overrides Sub Dispose(ByVal disposing As Boolean)
                Close()
            End Sub

            Public Overrides Sub Close()
                If responseStream IsNot Nothing Then
                    responseStream.Close()
                    responseStream = Nothing
                End If
                If webResponse IsNot Nothing Then
                    webResponse.Close()
                    webResponse = Nothing
                End If
            End Sub

            Public Overrides ReadOnly Property CanRead() As Boolean
                Get
                    Return True
                End Get
            End Property

            Public Overrides ReadOnly Property CanSeek() As Boolean
                Get
                    Return False
                End Get
            End Property

            Public Overrides ReadOnly Property CanWrite() As Boolean
                Get
                    Return False
                End Get
            End Property

            Public Overrides ReadOnly Property Length() As Long
                Get
                    Return streamLength
                End Get
            End Property

            Public Overrides Property Position() As Long
                Get
                    Return responseStream.Position
                End Get
                Set(ByVal value As Long)
                End Set
            End Property

            Public Overrides Function Read(ByVal buffer() As Byte, ByVal offset As Integer, ByVal count As Integer) As Integer
                Return responseStream.Read(buffer, offset, count)
            End Function

            Public Function ToArray() As Byte()
                Dim length As Integer = CInt(webResponse.ContentLength)
                If length <= 0 Then length = 4096
                Using memoryStream As New IO.MemoryStream(length)
                    Dim buffer(4095) As Byte
                    Do
                        Dim bytes As Integer = responseStream.Read(buffer, 0, 4096)
                        If bytes = 0 Then Exit Do
                        memoryStream.Write(buffer, 0, bytes)
                    Loop
                    Return memoryStream.ToArray()
                End Using
            End Function

            Public Overrides Function Seek(ByVal offset As Long, ByVal origin As IO.SeekOrigin) As Long
            End Function

            Public Overrides Sub SetLength(ByVal value As Long)
            End Sub

            Public Overrides Sub Flush()
            End Sub

            Public Overrides Sub Write(ByVal buffer() As Byte, ByVal offset As Integer, ByVal count As Integer)
            End Sub
        End Class  ' ConnectStream
    End Class  ' MadSonic
End Class


