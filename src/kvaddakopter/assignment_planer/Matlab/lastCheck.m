
%*************************************************************************
% Search for points in forbidden areas and put them on the edge
%*************************************************************************
function trajectory = lastCheck( trajectoryfullsize, object )
if not(isempty(object.forbiddenarea))
try
for ii = 1:length(object.forbiddenarea)
    forbiddenarea = object.forbiddenarea{ii};
    tic;
    while true
        inforbiddenarea = inpolygon(trajectoryfullsize(:,2),...
            trajectoryfullsize(:,1), forbiddenarea(:,2),...
            forbiddenarea(:,1));
        if not(any(inforbiddenarea))
            break
        elseif toc > 2
            break
        end
        ind = find( inforbiddenarea );
        
        index = ind(1); pointindexes = ind(1)-1;
        while inforbiddenarea(index) == 1
            pointindexes = [pointindexes; index];
            index = index + 1;
        end
        pointindexes = [pointindexes; index];
        
        % Sort points in both clockwise and counterclockwise order and evaluate
        % which one of them to have the shortest distance from startpoint to
        % endpoint.
        [xv_cw, yv_cw] = poly2cw(forbiddenarea(:,1), forbiddenarea(:,2));
        [xv_ccw, yv_ccw] = poly2ccw(forbiddenarea(:,1), forbiddenarea(:,2));
        
        % Interpolate points in polygon
        tmpcoord1 = interparc(1e2,xv_cw, yv_cw,'linear');
        xv_cw = tmpcoord1(1:end-1,1); yv_cw = tmpcoord1(1:end-1,2);
        tmpcoord2 = interparc(1e2,xv_ccw, yv_ccw,'linear');
        xv_ccw = tmpcoord2(1:end-1,1); yv_ccw = tmpcoord2(1:end-1,2);
        
        % Find the centrum point of the forbidden area
        centerpoint = [mean(forbiddenarea(:,1)) mean(forbiddenarea(:,2))];
        
        % Sort points from distance to first point and last, for cw and ccw
        d1 = sqrt((xv_cw-trajectoryfullsize(pointindexes(1),1)).^2 ...
            + (yv_cw-trajectoryfullsize(pointindexes(1),2)).^2);
        [~, index1] = min(d1);
        d2 = sqrt((xv_cw-trajectoryfullsize(pointindexes(end),1)).^2 ...
            + (yv_cw-trajectoryfullsize(pointindexes(end),2)).^2);
        [~, index2] = min(d2);
        
        % Sort the index
        rawindex = (1:length(xv_cw))';
        if index1 == index2
            index = index1;
        elseif index1 > index2
            index = [rawindex(index1:end); rawindex(1:index2)];
        else
            index = rawindex(index1:index2);
        end
            
        points_cw = [xv_cw(index) yv_cw(index)]...
            + 1e-3*([xv_cw(index) yv_cw(index)]...
            - repmat(centerpoint,length(index),1));
        
        d1 = sqrt((xv_ccw-trajectoryfullsize(pointindexes(1),1)).^2 ...
            + (yv_ccw-trajectoryfullsize(pointindexes(1),2)).^2);
        [~, index1] = min(d1);
        d2 = sqrt((xv_ccw-trajectoryfullsize(pointindexes(end),1)).^2 ...
            + (yv_ccw-trajectoryfullsize(pointindexes(end),2)).^2);
        [~, index2] = min(d2);
        
        % Sort the index
        rawindex = (1:length(xv_ccw))';
        if index1 == index2
            index = index1;
        elseif index1 > index2
            index = [rawindex(index1:end); rawindex(1:index2)];
        else
            index = rawindex(index1:index2);
        end
        
        points_ccw = [xv_ccw(index) yv_ccw(index)]...
            + 1e-3*([xv_ccw(index) yv_ccw(index)]...
            - repmat(centerpoint,length(index),1));
        
        % Calculate distances for the two choices of directions
        distance_cw = lldistkm(trajectoryfullsize(pointindexes(1),:),...
            points_cw(1,:))*1e3;
        for jj = 2:size(points_cw,1)
            distance_cw = distance_cw + lldistkm(points_cw(jj-1,:),...
                points_cw(jj,:))*1e3;
        end
        distance_cw = distance_cw + lldistkm(points_cw(end,:),...
            trajectoryfullsize(pointindexes(end),:))*1e3;
        
        distance_ccw = lldistkm(trajectoryfullsize(pointindexes(1),:),...
            points_ccw(1,:))*1e3;
        for jj = 2:size(points_ccw,1)
            distance_ccw = distance_ccw + lldistkm(points_ccw(jj-1,:),...
                points_ccw(jj,:))*1e3;
        end
        distance_ccw = distance_ccw + lldistkm(points_ccw(end,:),...
            trajectoryfullsize(pointindexes(end),:))*1e3;
        
        if distance_cw < distance_ccw
            points = points_cw;
        else
            points = points_ccw;
        end
        
        % Add the points to trajectory
        trajectoryfullsize = [trajectoryfullsize(1:pointindexes(1),:);...
            points; trajectoryfullsize(pointindexes(end):end,:)];
    end
end
catch
end
end

% Get points with the distance of maximum object.pointdistance
trajectory = trajectoryfullsize;
index = 2;
for ii = 2:size(trajectoryfullsize,1)
    localtrajectorylength = lldistkm(trajectoryfullsize(ii-1,:),...
        trajectoryfullsize(ii,:))*1e3;
    if localtrajectorylength > 1.1*object.pointdistance
        nrofpoints = floor(localtrajectorylength/object.pointdistance);
        points = interparc(2+nrofpoints,trajectoryfullsize(ii-1:ii,1),...
            trajectoryfullsize(ii-1:ii,2),'linear');
        trajectory = [trajectory(1:index-1,:); points(2:end-1,:);...
            trajectory(index:end,:)];
        index = index + nrofpoints + 1;
    else
        index = index + 1;
    end
end