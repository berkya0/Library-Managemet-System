document.addEventListener('DOMContentLoaded', function() {
    const token = localStorage.getItem('token');
    const userData = JSON.parse(localStorage.getItem('user'));
	const memberSearch = document.getElementById('memberSearch');
	   const filterRole = document.getElementById('filterRole');
	  
    
    if (!token || isTokenExpired(token)) {
        logout();
        return;
    }
	

    const apiBaseUrl = '/rest/api';
    const bookForm = document.getElementById('addBookForm');
    const bookTableBody = document.querySelector('#bookTable tbody');
    const memberTableBody = document.querySelector('#memberTable tbody');
    const logoutBtn = document.getElementById('logoutBtn');
    const tabButtons = document.querySelectorAll('.tab-btn');
    const tabContents = document.querySelectorAll('.tab-content');
    const membersTabBtn = document.querySelector('[data-tab="members"]');
    const allLoansTabBtn = document.querySelector('[data-tab="all-loans"]');
	const editProfileBtn = document.getElementById('editProfileBtn');
	const myLoansSearch = document.getElementById('myLoansSearch');
	  if (myLoansSearch) {
	      myLoansSearch.addEventListener('input', filterMyLoans);
	  }
	   if (editProfileBtn) {
	       editProfileBtn.addEventListener('click', openProfileEditModal);
	   }
	   const loanSearch = document.getElementById('loanSearch');
	   const filterStatus = document.getElementById('filterStatus');
	   let allLoans = [];
    
    const jwtService = {
        extractMemberId: function(token) {
            try {
                const payload = JSON.parse(atob(token.split('.')[1]));
                return payload.memberId || null;
            } catch (e) {
                return null;
            }
        }
    };
	if (loanSearch && filterStatus) {
	    loanSearch.addEventListener('input', filterLoans);
	    filterStatus.addEventListener('change', filterLoans);
	}
	
	if (memberSearch && filterRole) {
	       // Arama ve filtreleme fonksiyonları
	       function filterMembers() {
	           const searchText = memberSearch.value.toLowerCase();
	           const roleValue = filterRole.value;
	           
	           const rows = document.querySelectorAll('#memberTable tbody tr');
	           
	           rows.forEach(row => {
	               const name = row.cells[1].textContent.toLowerCase();
	               const email = row.cells[2].textContent.toLowerCase();
	               const phone = row.cells[3].textContent.toLowerCase();
	               const role = row.cells[5].textContent;
	               
	               const matchesSearch = name.includes(searchText) || 
	                                   email.includes(searchText) || 
	                                   phone.includes(searchText);
	               const matchesRole = roleValue === '' || role === roleValue;
	               
	               row.style.display = (matchesSearch && matchesRole) ? '' : 'none';
	           });
	       }
		 }
		 memberSearch.addEventListener('input', filterMembers);
		        filterRole.addEventListener('change', filterMembers);

    // Admin değilse kitap ekleme formunu ve admin sekmelerini gizle
    if (userData.role !== 'ADMIN') {
        bookForm?.remove();
        membersTabBtn?.remove();
        allLoansTabBtn?.remove();
    }

    // Sekme geçişleri
    tabButtons.forEach(button => {
        button.addEventListener('click', () => {
            const tabId = button.getAttribute('data-tab');
            
            tabButtons.forEach(btn => btn.classList.remove('active'));
            button.classList.add('active');
            
            tabContents.forEach(content => content.classList.remove('active'));
            document.getElementById(`${tabId}-tab`).classList.add('active');
            
            // Sekmeye özel yükleme işlemleri
            switch(tabId) {
                case 'profile':
                    loadProfile();
                    break;
                case 'books':
                    loadBooks();
                    break;
                case 'members':
                    loadMembers();
                    break;
                case 'all-loans':
                    loadAllLoans();
                    break;
            }
        });
    });

    // İlk yükleme
    loadProfile();
    
    if (bookForm) {
        bookForm.addEventListener('submit', function(e) {
            e.preventDefault();
            saveBook();
        });
    }
    
    if (logoutBtn) {
        logoutBtn.addEventListener('click', logout);
    }
    
	let allBooks = [];

	async function loadBooks() {
	    try {
	        showLoading(true);
	        const response = await fetch(`${apiBaseUrl}/book/get/list`, {
	            headers: {
	                'Authorization': `Bearer ${token}`,
	                'Content-Type': 'application/json'
	            }
	        });
	        
	        const result = await handleResponse(response);
	        
	        if (result && result.payload) {
	            allBooks = result.payload;
	            renderBooks(allBooks);
	            // Kitaplar yüklendikten sonra aramayı sıfırla
	            resetSearch();
	        }
	    } catch (error) {
	        showError('Kitaplar yüklenirken hata oluştu: ' + error.message);
	    } finally {
	        showLoading(false);
	    }
	}
	//bookSearch çubuğu
	document.getElementById('bookSearch')?.addEventListener('input', function(e) {
	    filterBooks();
	});

	document.getElementById('searchCategory')?.addEventListener('change', function(e) {
	    filterBooks();
	});
	
	function filterBooks() {
	    const searchTerm = document.getElementById('bookSearch').value.toLowerCase();
	    const categoryFilter = document.getElementById('searchCategory').value;
	    const rows = document.querySelectorAll('#bookTable tbody tr');
	    
	    rows.forEach(row => {
	        const bookTitle = row.querySelector('td:nth-child(2)').textContent.toLowerCase();
	        const bookAuthor = row.querySelector('td:nth-child(3)').textContent.toLowerCase();
	        const bookCategory = row.querySelector('td:nth-child(4)').textContent.toUpperCase();
	        const bookIsbn = row.querySelector('td:nth-child(5)').textContent.toLowerCase();
	        
	        // Arama terimi herhangi bir alanda eşleşiyor mu?
	        const matchesSearch = searchTerm === '' || 
	                            bookTitle.includes(searchTerm) ||
	                            bookAuthor.includes(searchTerm) ||
	                            bookCategory.includes(searchTerm) ||
	                            bookIsbn.includes(searchTerm);
	        
	        // Kategori filtresi eşleşiyor mu?
	        const matchesCategory = categoryFilter === '' || bookCategory === categoryFilter;
	        
	        if (matchesSearch && matchesCategory) {
	            row.style.display = '';
	        } else {
	            row.style.display = 'none';
	        }
	    });
	}
	function resetSearch() {
	    document.getElementById('bookSearch').value = '';
	    document.getElementById('searchCategory').value = '';
	    filterBooks();
	}
    
    async function loadMembers() {
        try {
            showLoading(true);
            const response = await fetch(`${apiBaseUrl}/member/get/list`, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            
            const result = await handleResponse(response);
            
            if (result && result.payload) {
                renderMembers(result.payload);
            }
        } catch (error) {
            showError('Üyeler yüklenirken hata oluştu: ' + error.message);
        } finally {
            showLoading(false);
        }
    }

	async function loadAllLoans() {
	    try {
	        showLoading(true);
	        const response = await fetch(`${apiBaseUrl}/loan/all`, {
	            headers: {
	                'Authorization': `Bearer ${token}`,
	                'Content-Type': 'application/json'
	            }
	        });
	        
	        const result = await handleResponse(response);
	        
	        if (result && result.payload) {
	            allLoans = result.payload;
	            renderAllLoans(allLoans);
	        }
	    } catch (error) {
	        showError('Ödünç listesi yüklenirken hata oluştu: ' + error.message);
	    } finally {
	        showLoading(false);
	    }
	}
	function filterLoans() {
	    const searchTerm = loanSearch.value.toLowerCase();
	    const statusFilter = filterStatus.value;
	    const rows = document.querySelectorAll('#allLoansTable tbody tr');
	    
	    rows.forEach(row => {
	        const memberName = row.cells[0].textContent.toLowerCase();
	        const bookTitle = row.cells[1].textContent.toLowerCase();
	        const bookAuthor = row.cells[2].textContent.toLowerCase();
	        const status = row.cells[6].textContent;
	        
	        const matchesSearch = memberName.includes(searchTerm) || 
	                            bookTitle.includes(searchTerm) ||
	                            bookAuthor.includes(searchTerm);
	        
	        const matchesStatus = statusFilter === '' || status === statusFilter;
	        
	        if (matchesSearch && matchesStatus) {
	            row.style.display = '';
	        } else {
	            row.style.display = 'none';
	        }
	    });
	}

	function renderAllLoans(loans) {
	    const tbody = document.querySelector('#allLoansTable tbody');
	    if (!tbody) return;
	    
	    tbody.innerHTML = '';
	    
	    if (loans.length === 0) {
	        tbody.innerHTML = '<tr><td colspan="7">Ödünç alınan kitap bulunamadı.</td></tr>';
	        return;
	    }

	    loans.forEach(loan => {
	        const row = document.createElement('tr');
	        const status = loan.returnDate ? 'İade Edildi' : 'Ödünç Alındı';
	        
	        row.innerHTML = `
	            <td>${loan.member?.fullName || '-'}</td>
	            <td>${loan.book?.title || '-'}</td>
	            <td>${loan.book?.author || '-'}</td>
	            <td>${loan.loanDate ? formatDate(loan.loanDate) : '-'}</td>
	            <td>${loan.dueDate ? formatDate(loan.dueDate) : '-'}</td>
	            <td>${loan.returnDate ? formatDate(loan.returnDate) : '-'}</td>
	            <td>${status}</td>
	        `;
	        tbody.appendChild(row);
	    });
	}
    
	function renderBooks(books) {
	    if (!bookTableBody) return;
	    
	    bookTableBody.innerHTML = '';
	    
	    books.forEach(book => {
	        const row = document.createElement('tr');
	        row.innerHTML = `
	            <td>${book.id || '-'}</td>
	            <td>${book.title || '-'}</td>
	            <td>${book.author || '-'}</td>
	            <td>${book.category || '-'}</td>
	            <td>${book.isbnNo || '-'}</td>
	            <td>${book.available ? 'Evet' : 'Hayır'}</td>
	            <td>
	                ${userData.role === 'ADMIN' 
	                    ? `<button class="btn-delete" data-id="${book.id}">Sil</button>` 
	                    : ''}
	                ${book.available 
	                    ? `<button class="btn-borrow" data-id="${book.id}">Ödünç Al</button>`
	                    : '-'}
	            </td>
	        `;
	        bookTableBody.appendChild(row);
	    });
	    
	    // Silme butonları için event listener ekle
	    document.querySelectorAll('.btn-delete').forEach(btn => {
	        btn.addEventListener('click', function() {
	            const bookId = this.getAttribute('data-id');
	            deleteBook(bookId);
	        });
	    });
	    
	    // Ödünç alma butonları için event listener
	    document.querySelectorAll('.btn-borrow').forEach(btn => {
	        btn.addEventListener('click', function() {
	            const bookId = this.getAttribute('data-id');
	            borrowBook(bookId);
	        });
	    });
	}
    
	function renderMembers(members) {
	    if (!memberTableBody) return;
	    
	    memberTableBody.innerHTML = '';
	    
	    members.forEach(member => {
	        const isAdmin = userData.role === 'ADMIN';
	        
	        const row = document.createElement('tr');
	        row.innerHTML = `
	            <td>${member.id || '-'}</td>
	            <td>${member.fullName || '-'}</td>
	            <td>${member.email || '-'}</td>
	            <td>${member.phoneNumber || '-'}</td>
	            <td>${member.user?.username || '-'}</td>
	            <td>${member.user?.role || '-'}</td>
	            <td>
	                ${isAdmin 
	                    ? `<button class="btn-edit-role" data-id="${member.id}">Rol Düzenle</button>
	                       <button class="btn-delete" data-id="${member.user?.id || member.id}">Sil</button>`
	                    : '-'}
	            </td>
	        `;
	        memberTableBody.appendChild(row);
	    });
	    
	    // Silme butonları için event listener ekle
	    document.querySelectorAll('.btn-delete').forEach(btn => {
	        btn.addEventListener('click', function() {
	            const userId = this.getAttribute('data-id');
	            deleteUser(userId);
	        });
	    });
	    
	    // Rol düzenleme butonları için event listener
	    document.querySelectorAll('.btn-edit-role').forEach(btn => {
	        btn.addEventListener('click', function() {
	            const memberId = this.getAttribute('data-id');
	            editMemberRole(memberId);
	        });
	    });
	}
    
	async function saveBook() {
	    const formData = {
	        title: document.getElementById('bookTitle').value,
	        author: document.getElementById('bookAuthor').value,
	        category: document.getElementById('bookCategory').value.toUpperCase(), // Kategoriyi büyük harfe çevir
	        isbnNo: document.getElementById('bookIsbn').value
	    };
	    
	    // ... fonksiyonun geri kalanı ...
	
	    
	       
	       try {
	           showLoading(true);
	           const response = await fetch(`${apiBaseUrl}/book/save`, {
	               method: 'POST',
	               headers: {
	                   'Authorization': `Bearer ${token}`,
	                   'Content-Type': 'application/json'
	               },
	               body: JSON.stringify(formData)
	           });
	           
	           const result = await handleResponse(response);
	           
	           showSuccess('Kitap başarıyla eklendi!');
	           bookForm.reset();
	           loadBooks();
	       } catch (error) {
	           showError('Kitap eklenirken hata oluştu: ' + error.message);
	       } finally {
	           showLoading(false);
	       }
	   }
    
	   async function deleteBook(bookId) {
	       if (!confirm('Bu kitabı silmek istediğinize emin misiniz?')) return;
	       
	       try {
	           showLoading(true);
	           const response = await fetch(`${apiBaseUrl}/book/delete/${bookId}`, {
	               method: 'DELETE',
	               headers: {
	                   'Authorization': `Bearer ${token}`,
	                   'Content-Type': 'application/json'
	               }
	           });
	           
	           await handleResponse(response);
	           
	           showSuccess('Kitap başarıyla silindi!');
	           loadBooks();
	       } catch (error) {
	           showError('Kitap silinirken hata oluştu: ' + error.message);
	       } finally {
	           showLoading(false);
	       }
	   }
    
	   async function deleteUser(userId) {
	       if (!confirm('Bu kullanıcıyı silmek istediğinize emin misiniz?')) return;
	       
	       try {
	           showLoading(true);
	           const response = await fetch(`${apiBaseUrl}/user/delete/${userId}`, {
	               method: 'DELETE',
	               headers: {
	                   'Authorization': `Bearer ${token}`,
	                   'Content-Type': 'application/json'
	               }
	           });
	           
	           await handleResponse(response);
	           
	           showSuccess('Kullanıcı başarıyla silindi!');
	           loadMembers();
	       } catch (error) {
	           showError('Kullanıcı silinirken hata oluştu: ' + error.message);
	       } finally {
	           showLoading(false);
	       }
	   }

	   // Üye silme fonksiyonunu güncelle (user silme ile aynı endpoint'i kullanacak)
	   async function deleteMember(memberId) {
	       if (!confirm('Bu üyeyi silmek istediğinize emin misiniz?')) return;
	       
	       try {
	           showLoading(true);
	           const response = await fetch(`${apiBaseUrl}/user/delete/${memberId}`, {
	               method: 'DELETE',
	               headers: {
	                   'Authorization': `Bearer ${token}`,
	                   'Content-Type': 'application/json'
	               }
	           });
	           
	           await handleResponse(response);
	           
	           showSuccess('Üye başarıyla silindi!');
	           loadMembers();
	       } catch (error) {
	           showError('Üye silinirken hata oluştu: ' + error.message);
	       } finally {
	           showLoading(false);
	       }
	   }
    
    async function editMemberRole(memberId) {
        try {
            showLoading(true);
            const response = await fetch(`${apiBaseUrl}/member/get/${memberId}`, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            
            const result = await handleResponse(response);
            
            if (result && result.payload) {
                openRoleEditModal(result.payload);
            }
        } catch (error) {
            showError('Üye bilgileri alınırken hata oluştu: ' + error.message);
        } finally {
            showLoading(false);
        }
    }
    
    function openRoleEditModal(member) {
        const modalHtml = `
            <div class="modal" id="editRoleModal">
                <div class="modal-content">
                    <span class="close">&times;</span>
                    <h3>Rol Düzenle</h3>
                    <form id="editRoleForm">
                        <input type="hidden" id="editMemberId" value="${member.id}">
                        <div class="form-group">
                            <label>Kullanıcı: ${member.user?.username || '-'}</label>
                        </div>
                        <div class="form-group">
                            <label>Mevcut Rol: ${member.user?.role || '-'}</label>
                        </div>
                        <div class="form-group">
                            <label for="newRole">Yeni Rol:</label>
                            <select id="newRole" required>
                                <option value="USER" ${member.user?.role === 'USER' ? 'selected' : ''}>USER</option>
                                <option value="ADMIN" ${member.user?.role === 'ADMIN' ? 'selected' : ''}>ADMIN</option>
                            </select>
                        </div>
                        <button type="submit">Güncelle</button>
                    </form>
                </div>
            </div>
        `;
        
        document.body.insertAdjacentHTML('beforeend', modalHtml);
        
        const modal = document.getElementById('editRoleModal');
        const closeBtn = modal.querySelector('.close');
        const editForm = document.getElementById('editRoleForm');
        
        modal.style.display = 'block';
        
        closeBtn.onclick = function() {
            modal.remove();
        };
        
        window.onclick = function(event) {
            if (event.target === modal) {
                modal.remove();
            }
        };
        
        editForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            await updateMemberRole(member.id);
            modal.remove();
        });
    }
	// Arama sonuç sayacı fonksiyonu (güncellenmiş)
	// Arama sonuç sayacı fonksiyonu (güncellenmiş)
	function updateSearchResultsCount(visibleCount) {
	    const totalCount = allBooks.length;
	    const searchHeader = document.querySelector('#books-tab h3');
	    
	    if (searchHeader) {
	        if (document.getElementById('bookSearch').value || document.getElementById('searchCategory').value) {
	            searchHeader.innerHTML = `Kitap Listesi <span style="font-size: 14px; color: #666; margin-left: 10px;">(${visibleCount}/${totalCount} kitap)</span>`;
	            
	            // Temizleme butonu ekle
	            if (!document.getElementById('clearSearchBtn')) {
	                const clearBtn = document.createElement('button');
	                clearBtn.id = 'clearSearchBtn';
	                clearBtn.textContent = 'Temizle';
	                clearBtn.classList.add('btn-clear');
	                clearBtn.addEventListener('click', resetSearch);
	                document.querySelector('.books-search-section').appendChild(clearBtn);
	            }
	        } else {
	            searchHeader.innerHTML = `Kitap Listesi <span style="font-size: 14px; color: #666; margin-left: 10px;">(${totalCount} kitap)</span>`;
	            
	            // Temizleme butonunu kaldır
	            const clearBtn = document.getElementById('clearSearchBtn');
	            if (clearBtn) clearBtn.remove();
	        }
	    }
	}
	
    
    async function updateMemberRole(memberId) {
        try {
            showLoading(true);
            const newRole = document.getElementById('newRole').value;
            
            const response = await fetch(`${apiBaseUrl}/member/update-role/${memberId}`, {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ role: newRole })
            });
            
            await handleResponse(response);
            showSuccess('Rol başarıyla güncellendi!');
            loadMembers();
        } catch (error) {
            showError('Rol güncellenirken hata oluştu: ' + error.message);
        } finally {
            showLoading(false);
        }
    }
    
    async function borrowBook(bookId) {
        if (!confirm('Bu kitabı ödünç almak istediğinize emin misiniz?')) return;
        
        try {
            const memberId = jwtService.extractMemberId(token);
            if (!memberId) {
                throw new Error("Üye bilgileriniz eksik. Lütfen tekrar giriş yapın.");
            }

            showLoading(true);
            const response = await fetch(`${apiBaseUrl}/loan/borrow`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    bookId: bookId,
                    memberId: memberId
                })
            });
            
            const result = await handleResponse(response);
            
            showSuccess('Kitap başarıyla ödünç alındı!');
            loadBooks();
            loadProfile();
        } catch (error) {
            console.error('Ödünç alma hatası:', error);
            showError('Kitap ödünç alınırken hata oluştu: ' + error.message);
        } finally {
            showLoading(false);
        }
    }

    async function loadProfile() {
        showLoading(true);

        try {
            const memberResponse = await fetch(`${apiBaseUrl}/member/me`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            const memberData = await handleResponse(memberResponse);
            renderProfile(memberData.payload);
        } catch (e) {
            showError("Profil bilgileri alınamadı: " + e.message);
        }

        try {
            const loansResponse = await fetch(`${apiBaseUrl}/loan/my-loans`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            const loansData = await handleResponse(loansResponse);
            renderMyLoans(loansData.payload || []);
        } catch (e) {
            console.warn("Loan yüklenemedi:", e.message);
            renderMyLoans([]); // boş liste göster
        }

        showLoading(false);
    }


    function renderProfile(member) {
        const profileDiv = document.getElementById('profileInfo');
        if (!profileDiv) return;
        
        profileDiv.innerHTML = `
            <p><strong>Ad Soyad:</strong> ${member.fullName || '-'}</p>
            <p><strong>Email:</strong> ${member.email || '-'}</p>
            <p><strong>Telefon:</strong> ${member.phoneNumber || '-'}</p>
            <p><strong>Üyelik Tarihi:</strong> ${member.membershipDate ? new Date(member.membershipDate).toLocaleDateString() : '-'}</p>
            <p><strong>Kullanıcı Adı:</strong> ${member.user?.username || '-'}</p>
            <p><strong>Rol:</strong> ${member.user?.role || '-'}</p>
        `;
    }

	function renderMyLoans(loans) {
	    const tbody = document.querySelector('#myLoansTable tbody');
	    if (!tbody) return;
	    
	    tbody.innerHTML = '';

	    const activeLoans = loans.filter(loan => !loan.returnDate);

	    if (activeLoans.length === 0) {
	        tbody.innerHTML = '<tr><td colspan="5">Ödünç alınan kitap bulunamadı.</td></tr>';
	        updateMyLoansSearchResults(); // Sonuç sayacını güncelle
	        return;
	    }

	    activeLoans.forEach(loan => {
	        const row = document.createElement('tr');
	        row.innerHTML = `
	            <td>${loan.book?.title || '-'}</td>
	            <td>${loan.book?.author || '-'}</td>
	            <td>${loan.loanDate ? formatDate(loan.loanDate) : '-'}</td>
	            <td>${loan.dueDate ? formatDate(loan.dueDate) : '-'}</td>
	            <td>
	                <button class="btn-return" data-id="${loan.id}">İade Et</button>
	            </td>
	        `;
	        tbody.appendChild(row);
	    });

	    document.querySelectorAll('.btn-return').forEach(btn => {
	        btn.addEventListener('click', function() {
	            const loanId = this.getAttribute('data-id');
	            returnBook(loanId);
	        });
	    });
	    
	    // Sonuç sayacını güncelle
	    updateMyLoansSearchResults();
	}

    function formatDate(dateString) {
        if (!dateString) return '-';
        
        try {
            const date = new Date(dateString);
            return date.toLocaleDateString('tr-TR', {
                year: 'numeric',
                month: 'long',
                day: 'numeric'
            });
        } catch (e) {
            console.error('Tarih formatlama hatası:', e);
            return dateString;
        }
    }

    async function returnBook(loanId) {
        if (!confirm('Bu kitabı iade etmek istediğinize emin misiniz?')) return;
        
        try {
            showLoading(true);
            const response = await fetch(`${apiBaseUrl}/loan/return/${loanId}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            
            await handleResponse(response);
            
            showSuccess('Kitap başarıyla iade edildi!');
            loadProfile();
        } catch (error) {
            showError('Kitap iade edilirken hata oluştu: ' + error.message);
        } finally {
            showLoading(false);
        }
    }
	// ... mevcut fonksiyonlar ...

	// YENİ EKLENEN FONKSİYONLAR (sayfanın sonuna ekleyin)
	function openProfileEditModal() {
	    const memberId = jwtService.extractMemberId(token);
	    if (!memberId) {
	        showError('Üye bilgileriniz alınamadı. Lütfen tekrar giriş yapın.');
	        return;
	    }

	    // Profil bilgilerini al
	    fetch(`${apiBaseUrl}/member/get/${memberId}`, {
	        headers: {
	            'Authorization': `Bearer ${token}`,
	            'Content-Type': 'application/json'
	        }
	    })
	    .then(response => handleResponse(response))
	    .then(result => {
	        if (result && result.payload) {
	            const member = result.payload;
	            const modalHtml = `
	                <div class="modal" id="editProfileModal">
	                    <div class="modal-content">
	                        <span class="close">&times;</span>
	                        <h3>Profili Düzenle</h3>
	                        <form id="editProfileForm">
	                            <input type="hidden" id="editMemberId" value="${member.id}">
	                            <input type="text" id="editFullName" placeholder="Ad Soyad" value="${member.fullName || ''}" required>
	                            <input type="email" id="editEmail" placeholder="Email" value="${member.email || ''}" required>
	                            <input type="text" id="editPhoneNumber" placeholder="Telefon (05xxxxxxxxx)" value="${member.phoneNumber || ''}" required>
	                            <button type="submit">Güncelle</button>
	                        </form>
	                    </div>
	                </div>
	            `;
	            
	            document.body.insertAdjacentHTML('beforeend', modalHtml);
	            
	            const modal = document.getElementById('editProfileModal');
	            const closeBtn = modal.querySelector('.close');
	            const editForm = document.getElementById('editProfileForm');
	            
	            modal.style.display = 'block';
	            
	            closeBtn.onclick = function() {
	                modal.remove();
	            };
	            
	            window.onclick = function(event) {
	                if (event.target === modal) {
	                    modal.remove();
	                }
	            };
	            
	            editForm.addEventListener('submit', function(e) {
	                e.preventDefault();
	                updateProfile(member.id);
	                modal.remove();
	            });
	        }
	    })
	    .catch(error => {
	        showError('Profil bilgileri alınırken hata oluştu: ' + error.message);
	    });
	}

	async function updateProfile(memberId) {
	    try {
	        showLoading(true);
	        const formData = {
	            fullName: document.getElementById('editFullName').value,
	            email: document.getElementById('editEmail').value,
	            phoneNumber: document.getElementById('editPhoneNumber').value
	        };
	        
	        const response = await fetch(`${apiBaseUrl}/member/update/${memberId}`, {
	            method: 'PUT',
	            headers: {
	                'Authorization': `Bearer ${token}`,
	                'Content-Type': 'application/json'
	            },
	            body: JSON.stringify(formData)
	        });
	        
	        await handleResponse(response);
	        
	        showSuccess('Profil başarıyla güncellendi!');
	        loadProfile();
	    } catch (error) {
	        showError('Profil güncellenirken hata oluştu: ' + error.message);
	    } finally {
	        showLoading(false);
	    }
	}

	// ... varsa diğer mevcut fonksiyonlar ...
    
    function isTokenExpired(token) {
        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            return payload.exp * 1000 < Date.now();
        } catch (e) {
            return true;
        }
    }
    
    function logout() {
        localStorage.clear();
        window.location.href = '/login.html';
    }
    
    async function handleResponse(response) {
        const data = await response.json();
        if (!response.ok) {
            throw new Error(data.errorMessage || `HTTP error! status: ${response.status}`);
        }
        return data;
    }
	function updateMyLoansSearchResults() {
	    const searchTerm = document.getElementById('myLoansSearch').value;
	    const visibleRows = document.querySelectorAll('#myLoansTable tbody tr:not([style*="display: none"])');
	    const totalRows = document.querySelectorAll('#myLoansTable tbody tr').length;
	    
	    const loansHeader = document.querySelector('#profile-tab h3:nth-of-type(2)');
	    
	    if (loansHeader && searchTerm) {
	        loansHeader.innerHTML = `Ödünç Aldığım Kitaplar <span style="font-size: 14px; color: #666; margin-left: 10px;">(${visibleRows.length}/${totalRows} kitap)</span>`;
	    } else if (loansHeader) {
	        loansHeader.innerHTML = `Ödünç Aldığım Kitaplar <span style="font-size: 14px; color: #666; margin-left: 10px;">(${totalRows} kitap)</span>`;
	    }
	}
	function filterMyLoans() {
	    const searchTerm = document.getElementById('myLoansSearch').value.toLowerCase();
	    const rows = document.querySelectorAll('#myLoansTable tbody tr');
	    
	    rows.forEach(row => {
	        const bookTitle = row.cells[0].textContent.toLowerCase();
	        const bookAuthor = row.cells[1].textContent.toLowerCase();
	        
	        const matchesSearch = searchTerm === '' || 
	                            bookTitle.includes(searchTerm) ||
	                            bookAuthor.includes(searchTerm);
	        
	        if (matchesSearch) {
	            row.style.display = '';
	        } else {
	            row.style.display = 'none';
	        }
	    });
	    
	    // Görünen sonuç sayısını güncelle
	    updateMyLoansSearchResults();
	}
    
    function showLoading(show) {
        const loader = document.getElementById('loadingSpinner');
        if (loader) {
            loader.style.display = show ? 'block' : 'none';
        }
    }
    
    function showSuccess(message) {
        alert(message);
    }
    
    function showError(message) {
        alert(message);
    }
});