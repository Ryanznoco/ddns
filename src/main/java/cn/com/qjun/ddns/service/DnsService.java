package cn.com.qjun.ddns.service;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.alidns.model.v20150109.AddDomainRecordRequest;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsRequest;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsResponse;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordRequest;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Ryan
 * @date 2018/5/5
 */
@Service
public class DnsService {
    private static final String TYPE_A = "A";

    @Autowired
    private IAcsClient iAcsClient;

    public void addRecord(String domainName, String rr, String value) throws ClientException {
        AddDomainRecordRequest request = new AddDomainRecordRequest();
        request.setDomainName(domainName);
        request.setRR(rr);
        request.setType(TYPE_A);
        request.setValue(value);
        iAcsClient.getAcsResponse(request);
    }

    public void updateRecord(String recordId, String rr, String value) throws ClientException {
        UpdateDomainRecordRequest request = new UpdateDomainRecordRequest();
        request.setRecordId(recordId);
        request.setRR(rr);
        request.setType(TYPE_A);
        request.setValue(value);
        iAcsClient.getAcsResponse(request);
    }

    public List<DescribeDomainRecordsResponse.Record> getRecordList(String domainName) throws ClientException {
        DescribeDomainRecordsRequest request = new DescribeDomainRecordsRequest();
        request.setDomainName(domainName);
        request.setTypeKeyWord(TYPE_A);
        DescribeDomainRecordsResponse recordsResponse = iAcsClient.getAcsResponse(request);
        return recordsResponse.getDomainRecords();
    }
}
